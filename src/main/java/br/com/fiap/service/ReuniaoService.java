package br.com.fiap.service;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ReuniaoDTO;
import br.com.fiap.model.entity.Analise;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.entity.Transcricao;
import br.com.fiap.model.repository.AnaliseRepository;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.model.repository.TranscricaoRepository;
import br.com.fiap.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ReuniaoService {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private AnaliseService analiseService;

    public Reuniao cadastrar(ReuniaoDTO reuniaoDTO) throws NegocioException {
        Reuniao reuniao = new Reuniao(reuniaoDTO);
        validar(reuniao);
        return reuniaoRepository.save(reuniao);
    }

    public Reuniao atualizar(Long codigo, ReuniaoDTO reuniaoDTO) throws NegocioException {
        Reuniao reuniaoAtual = reuniaoRepository.findByCodigo(codigo);
        if ("CANCELADA".equals(reuniaoAtual.getStatus())) {
            throw new NegocioException("Reunião cancelada não pode ser alterada. Reagende-a primeiro.");
        }
        if (!reuniaoAtual.getTipo().equals(reuniaoDTO.tipo())) {
            throw new NegocioException("Não é permitido alterar o tipo da reunião.");
        }
        Reuniao reuniao = new Reuniao(reuniaoDTO);
        reuniao.setCodigo(codigo);
        reuniao.setStatus(reuniaoAtual.getStatus());
        reuniao.setProcessada(reuniaoAtual.getProcessada());
        validar(reuniao);
        return reuniaoRepository.save(reuniao);
    }

    public Reuniao alterarStatus(Long codigo, String novoStatus) throws NegocioException {
        Reuniao reuniao = reuniaoRepository.findByCodigo(codigo);
        if (!reuniao.podeMudarStatus(novoStatus)) {
            throw new NegocioException("Não é permitido alterar o status de " + reuniao.getStatus() + " para " + novoStatus + ".");
        }
        reuniao.setStatus(novoStatus);
        return reuniaoRepository.save(reuniao);
    }

    // Apaga tambem a transcricao e a analise da reuniao
    public void excluir(Long codigo) {
        Transcricao transcricao = transcricaoRepository.findByCodigoReuniao(codigo);
        if (transcricao != null) {
            transcricaoRepository.delete(transcricao);
        }
        Analise analise = analiseRepository.findByCodigoReuniao(codigo);
        if (analise != null) {
            analiseRepository.delete(analise);
        }
        reuniaoRepository.deleteById(codigo);
    }

    // Fila de atendimento: reunioes nao canceladas com prioridade >= 50, da maior para a menor
    public List<Reuniao> listarPrioritarias() {
        return reuniaoRepository.findAll().stream()
                .filter(r -> !"CANCELADA".equals(r.getStatus()))
                .filter(r -> r.calcularPrioridade() >= 50)
                .sorted(Comparator.comparingDouble(Reuniao::calcularPrioridade).reversed())
                .toList();
    }

    // Salva a transcricao, gera a analise e atualiza os indicadores da reuniao
    public Analise enviarTranscricao(Long codigo, String texto) throws NegocioException {
        Reuniao reuniao = reuniaoRepository.findByCodigo(codigo);
        if ("CANCELADA".equals(reuniao.getStatus())) {
            throw new NegocioException("Não é possível enviar a transcrição de uma reunião cancelada.");
        }

        Transcricao transcricao = new Transcricao(codigo, texto);
        Transcricao transcricaoAnterior = transcricaoRepository.findByCodigoReuniao(codigo);
        if (transcricaoAnterior != null) {
            transcricao.setCodigo(transcricaoAnterior.getCodigo());
        }
        transcricaoRepository.save(transcricao);

        Analise analise = analiseService.analisar(texto);
        analise.setCodigoReuniao(codigo);
        Analise analiseAnterior = analiseRepository.findByCodigoReuniao(codigo);
        if (analiseAnterior != null) {
            analise.setCodigo(analiseAnterior.getCodigo());
        }
        analiseRepository.save(analise);

        aplicarAnalise(reuniao, analise);
        reuniao.setStatus("REALIZADA");
        reuniao.setProcessada(true);
        reuniaoRepository.save(reuniao);
        return analise;
    }

    // Regra de negocio: o resultado da analise atualiza os indicadores da reuniao
    private void aplicarAnalise(Reuniao reuniao, Analise analise) {
        if ("VENDAS".equals(reuniao.getTipo())) {
            reuniao.setScoreRisco(Math.round((reuniao.getScoreRisco() + analise.getRiscoChurn()) / 2.0f));
            if (analise.getConcorrentes() != null) {
                reuniao.setConcorrente(true);
            }
        } else if (analise.getRiscoChurn() >= 60) {
            reuniao.setSinalChurn(true);
        }
    }

    private void validar(Reuniao reuniao) throws NegocioException {
        if (usuarioRepository.findByCodigo(reuniao.getCodigoUsuario()) == null) {
            throw new NegocioException("Usuário responsável (código " + reuniao.getCodigoUsuario() + ") não encontrado.");
        }
        if (clienteRepository.findByCodigo(reuniao.getCodigoCliente()) == null) {
            throw new NegocioException("Cliente (código " + reuniao.getCodigoCliente() + ") não encontrado.");
        }
        Reuniao mesmoHorario = reuniaoRepository.findByCodigoClienteAndDataHora(reuniao.getCodigoCliente(), reuniao.getDataHora());
        if (mesmoHorario != null && !mesmoHorario.getCodigo().equals(reuniao.getCodigo())) {
            throw new NegocioException("O cliente já possui uma reunião marcada neste horário.");
        }

        if ("VENDAS".equals(reuniao.getTipo())) {
            if (reuniao.getValorOportunidade() == null) {
                throw new NegocioException("Informe o valor da oportunidade para reuniões de VENDAS.");
            }
            if (reuniao.getScoreRisco() == null) {
                reuniao.setScoreRisco(0);
            }
            if (reuniao.getConcorrente() == null) {
                reuniao.setConcorrente(false);
            }
            reuniao.setNps(null);
            reuniao.setReclamacoes(null);
            reuniao.setSinalChurn(null);
        } else {
            if (reuniao.getNps() == null) {
                throw new NegocioException("Informe a nota NPS (0 a 10) para reuniões de CS.");
            }
            if (reuniao.getReclamacoes() == null) {
                reuniao.setReclamacoes(0);
            }
            if (reuniao.getSinalChurn() == null) {
                reuniao.setSinalChurn(false);
            }
            reuniao.setValorOportunidade(null);
            reuniao.setScoreRisco(null);
            reuniao.setConcorrente(null);
        }
    }
}
