package br.com.fiap.reunia.service;

import br.com.fiap.reunia.dao.ClienteDAO;
import br.com.fiap.reunia.dao.ReuniaoDAO;
import br.com.fiap.reunia.dao.UsuarioDAO;
import br.com.fiap.reunia.dto.ReuniaoRequest;
import br.com.fiap.reunia.dto.ReuniaoResponse;
import br.com.fiap.reunia.exception.ConflitoException;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.exception.RecursoNaoEncontradoException;
import br.com.fiap.reunia.model.Reuniao;
import br.com.fiap.reunia.model.ReuniaoCS;
import br.com.fiap.reunia.model.ReuniaoVendas;
import br.com.fiap.reunia.model.StatusReuniao;
import br.com.fiap.reunia.model.TipoReuniao;
import br.com.fiap.reunia.util.ValidadorUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import static br.com.fiap.reunia.factory.GerenciadorTransacao.consultar;
import static br.com.fiap.reunia.factory.GerenciadorTransacao.executarEmTransacao;

/**
 * Regras de negocio das reunioes: validacao por tipo, conflito de agenda, ciclo de status e fila de prioridade.
 */
@ApplicationScoped
public class ReuniaoService {

    public List<ReuniaoResponse> listar(TipoReuniao tipo, StatusReuniao status, Long clienteId, Long usuarioId) {
        return consultar(con -> new ReuniaoDAO(con).listar(tipo, status, clienteId, usuarioId)).stream()
                .map(ReuniaoResponse::de)
                .toList();
    }

    /** Fila de atendimento: reunioes nao canceladas com prioridade >= minimo, da maior para a menor. */
    public List<ReuniaoResponse> listarPrioritarias(double minimo) {
        if (minimo < 0 || minimo > 100) {
            throw new NegocioException("A prioridade mínima deve estar entre 0 e 100.");
        }
        return consultar(con -> new ReuniaoDAO(con).listar(null, null, null, null)).stream()
                .filter(r -> r.getStatus() != StatusReuniao.CANCELADA)
                .filter(r -> r.calcularPrioridade() >= minimo)
                .sorted(Comparator.comparingDouble(Reuniao::calcularPrioridade).reversed())
                .map(ReuniaoResponse::de)
                .toList();
    }

    public ReuniaoResponse buscarPorId(long id) {
        return ReuniaoResponse.de(consultar(con -> buscarOuFalhar(new ReuniaoDAO(con), id)));
    }

    public ReuniaoResponse cadastrar(ReuniaoRequest request) {
        validarCorpo(request);
        TipoReuniao tipo = ValidadorUtil.obrigatorio(request.tipo(), "tipo");
        Reuniao reuniao = tipo == TipoReuniao.VENDAS ? new ReuniaoVendas() : new ReuniaoCS();
        preencher(request, reuniao);

        return ReuniaoResponse.de(executarEmTransacao(con -> {
            validarRelacionamentos(con, reuniao);
            return new ReuniaoDAO(con).inserir(reuniao);
        }));
    }

    public ReuniaoResponse atualizar(long id, ReuniaoRequest request) {
        validarCorpo(request);
        return ReuniaoResponse.de(executarEmTransacao(con -> {
            ReuniaoDAO dao = new ReuniaoDAO(con);
            Reuniao reuniao = buscarOuFalhar(dao, id);
            if (reuniao.getStatus() == StatusReuniao.CANCELADA) {
                throw new NegocioException("Reunião cancelada não pode ser editada. Reagende-a antes (status AGENDADA).");
            }
            if (request.tipo() != null && request.tipo() != reuniao.getTipo()) {
                throw new NegocioException("Não é permitido alterar o tipo da reunião. Cadastre uma nova reunião.");
            }
            preencher(request, reuniao);
            validarRelacionamentos(con, reuniao);
            dao.atualizar(reuniao);
            return reuniao;
        }));
    }

    public ReuniaoResponse alterarStatus(long id, StatusReuniao novoStatus) {
        return ReuniaoResponse.de(executarEmTransacao(con -> {
            ReuniaoDAO dao = new ReuniaoDAO(con);
            Reuniao reuniao = buscarOuFalhar(dao, id);
            reuniao.alterarStatus(novoStatus);
            dao.atualizar(reuniao);
            return reuniao;
        }));
    }

    /** Exclui a reuniao; transcricao e analise sao removidas em cascata (ON DELETE CASCADE). */
    public void excluir(long id) {
        executarEmTransacao(con -> {
            if (!new ReuniaoDAO(con).excluir(id)) {
                throw new RecursoNaoEncontradoException("Reunião", id);
            }
            return null;
        });
    }

    static Reuniao buscarOuFalhar(ReuniaoDAO dao, long id) throws SQLException {
        return dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Reunião", id));
    }

    private void validarCorpo(ReuniaoRequest request) {
        if (request == null) {
            throw new NegocioException("Corpo da requisição é obrigatório.");
        }
    }

    private void validarRelacionamentos(Connection con, Reuniao reuniao) throws SQLException {
        if (new UsuarioDAO(con).buscarPorId(reuniao.getUsuarioId()).isEmpty()) {
            throw new NegocioException("Usuário responsável (id " + reuniao.getUsuarioId() + ") não existe.");
        }
        if (new ClienteDAO(con).buscarPorId(reuniao.getClienteId()).isEmpty()) {
            throw new NegocioException("Cliente (id " + reuniao.getClienteId() + ") não existe.");
        }
        if (new ReuniaoDAO(con).existeConflitoAgenda(reuniao.getClienteId(), reuniao.getDataHora(), reuniao.getId())) {
            throw new ConflitoException("O cliente já possui uma reunião marcada para " + reuniao.getDataHora() + ".");
        }
    }

    private void preencher(ReuniaoRequest request, Reuniao reuniao) {
        reuniao.setTitulo(ValidadorUtil.textoObrigatorio(request.titulo(), "titulo", 3, 150));
        reuniao.setDataHora(ValidadorUtil.obrigatorio(request.dataHora(), "dataHora"));
        reuniao.setDuracaoMinutos(ValidadorUtil.intervalo(request.duracaoMinutos(), "duracaoMinutos", 1, 1440));
        reuniao.setUsuarioId(ValidadorUtil.obrigatorio(request.usuarioId(), "usuarioId"));
        reuniao.setClienteId(ValidadorUtil.obrigatorio(request.clienteId(), "clienteId"));

        if (reuniao instanceof ReuniaoVendas vendas) {
            BigDecimal valor = ValidadorUtil.obrigatorio(request.valorOportunidade(), "valorOportunidade");
            if (valor.signum() < 0) {
                throw new NegocioException("O campo 'valorOportunidade' não pode ser negativo.");
            }
            vendas.setValorOportunidade(valor);
            vendas.setScoreRisco(request.scoreRisco() == null ? 0 : ValidadorUtil.intervalo(request.scoreRisco(), "scoreRisco", 0, 100));
            vendas.setConcorrente(Boolean.TRUE.equals(request.concorrente()));
        } else if (reuniao instanceof ReuniaoCS cs) {
            cs.setNps(ValidadorUtil.intervalo(request.nps(), "nps", 0, 10));
            cs.setReclamacoes(request.reclamacoes() == null ? 0 : ValidadorUtil.intervalo(request.reclamacoes(), "reclamacoes", 0, 9999));
            cs.setSinalChurn(Boolean.TRUE.equals(request.sinalChurn()));
        }
    }
}
