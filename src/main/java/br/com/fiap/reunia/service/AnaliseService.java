package br.com.fiap.reunia.service;

import br.com.fiap.reunia.dao.AnaliseDAO;
import br.com.fiap.reunia.dao.ReuniaoDAO;
import br.com.fiap.reunia.dao.TranscricaoDAO;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.exception.RecursoNaoEncontradoException;
import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.Reuniao;
import br.com.fiap.reunia.model.Transcricao;
import br.com.fiap.reunia.util.TextoUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static br.com.fiap.reunia.factory.GerenciadorTransacao.consultar;
import static br.com.fiap.reunia.factory.GerenciadorTransacao.executarEmTransacao;

/**
 * Fluxo principal da Reun.IA: recebe a transcricao, gera a analise e atualiza os indicadores da reuniao,
 * tudo na mesma transacao.
 */
@ApplicationScoped
public class AnaliseService {

    static final int TAMANHO_MINIMO = 20;
    static final int TAMANHO_MAXIMO = 100_000;

    @Inject
    MotorAnaliseService motor;

    public AnaliseReuniao enviarTranscricao(long reuniaoId, String texto) {
        String conteudo = validarTexto(texto);
        return executarEmTransacao(con -> {
            ReuniaoDAO reuniaoDAO = new ReuniaoDAO(con);
            Reuniao reuniao = ReuniaoService.buscarOuFalhar(reuniaoDAO, reuniaoId);
            reuniao.registrarTranscricao();

            new TranscricaoDAO(con).salvar(new Transcricao(null, reuniaoId, conteudo, TextoUtil.contarPalavras(conteudo), null));

            AnaliseReuniao analise = motor.analisar(conteudo);
            analise.setReuniaoId(reuniaoId);
            AnaliseReuniao salva = new AnaliseDAO(con).salvar(analise);

            reuniao.aplicarAnalise(salva);
            reuniaoDAO.atualizar(reuniao);
            return salva;
        });
    }

    /** Analisa um texto sem gravar nada (usado pelo Front-End para pre-visualizar o resultado). */
    public AnaliseReuniao simular(String texto) {
        return motor.analisar(validarTexto(texto));
    }

    public Transcricao buscarTranscricao(long reuniaoId) {
        return consultar(con -> new TranscricaoDAO(con).buscarPorReuniao(reuniaoId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transcrição da reunião", reuniaoId));
    }

    public AnaliseReuniao buscarAnalise(long reuniaoId) {
        return consultar(con -> new AnaliseDAO(con).buscarPorReuniao(reuniaoId))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Análise da reunião", reuniaoId));
    }

    private String validarTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new NegocioException("O campo 'texto' da transcrição é obrigatório.");
        }
        String conteudo = texto.trim();
        if (conteudo.length() < TAMANHO_MINIMO || conteudo.length() > TAMANHO_MAXIMO) {
            throw new NegocioException("A transcrição deve ter entre " + TAMANHO_MINIMO + " e " + TAMANHO_MAXIMO + " caracteres.");
        }
        return conteudo;
    }
}
