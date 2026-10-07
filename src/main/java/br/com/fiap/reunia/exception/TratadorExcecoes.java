package br.com.fiap.reunia.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Converte as excecoes da aplicacao em respostas HTTP com corpo {@link ErroResponse}.
 * Assim os Resources ficam livres de try/catch e o Front-End sempre recebe o mesmo formato de erro.
 */
public class TratadorExcecoes {

    private static final Logger LOG = Logger.getLogger(TratadorExcecoes.class);

    @ServerExceptionMapper
    public Response negocio(NegocioException e) {
        return resposta(Response.Status.BAD_REQUEST, e.getMessage());
    }

    @ServerExceptionMapper
    public Response naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(Response.Status.NOT_FOUND, e.getMessage());
    }

    @ServerExceptionMapper
    public Response conflito(ConflitoException e) {
        return resposta(Response.Status.CONFLICT, e.getMessage());
    }

    @ServerExceptionMapper
    public Response autenticacao(AutenticacaoException e) {
        return resposta(Response.Status.UNAUTHORIZED, e.getMessage());
    }

    @ServerExceptionMapper
    public Response bancoDeDados(BancoDeDadosException e) {
        LOG.error(e.getMessage(), e.getCause());
        return resposta(Response.Status.INTERNAL_SERVER_ERROR,
                e.getMessage() + " Verifique a conexão com o Oracle e tente novamente.");
    }

    @ServerExceptionMapper
    public Response webApplication(WebApplicationException e) {
        return resposta(e.getResponse().getStatusInfo(), e.getMessage());
    }

    @ServerExceptionMapper
    public Response inesperado(Exception e) {
        LOG.error("Erro inesperado na API", e);
        return resposta(Response.Status.INTERNAL_SERVER_ERROR, "Erro interno inesperado. Tente novamente mais tarde.");
    }

    private Response resposta(Response.StatusType status, String mensagem) {
        return Response.status(status.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(ErroResponse.of(status.getStatusCode(), status.getReasonPhrase(), mensagem))
                .build();
    }
}
