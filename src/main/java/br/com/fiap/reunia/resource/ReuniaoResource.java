package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.ReuniaoRequest;
import br.com.fiap.reunia.dto.ReuniaoResponse;
import br.com.fiap.reunia.dto.StatusRequest;
import br.com.fiap.reunia.dto.TranscricaoRequest;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.model.StatusReuniao;
import br.com.fiap.reunia.model.TipoReuniao;
import br.com.fiap.reunia.model.Transcricao;
import br.com.fiap.reunia.service.AnaliseService;
import br.com.fiap.reunia.service.ReuniaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/reunioes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reuniões")
public class ReuniaoResource {

    @Inject
    ReuniaoService reuniaoService;

    @Inject
    AnaliseService analiseService;

    @GET
    public List<ReuniaoResponse> listar(@QueryParam("tipo") TipoReuniao tipo,
                                        @QueryParam("status") StatusReuniao status,
                                        @QueryParam("clienteId") Long clienteId,
                                        @QueryParam("usuarioId") Long usuarioId) {
        return reuniaoService.listar(tipo, status, clienteId, usuarioId);
    }

    @GET
    @Path("/prioritarias")
    public List<ReuniaoResponse> prioritarias(@QueryParam("minimo") @DefaultValue("50") double minimo) {
        return reuniaoService.listarPrioritarias(minimo);
    }

    @GET
    @Path("/{id}")
    public ReuniaoResponse buscar(@PathParam("id") long id) {
        return reuniaoService.buscarPorId(id);
    }

    @POST
    public Response cadastrar(ReuniaoRequest request, @Context UriInfo uriInfo) {
        ReuniaoResponse criada = reuniaoService.cadastrar(request);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(criada.id())).build())
                .entity(criada)
                .build();
    }

    @PUT
    @Path("/{id}")
    public ReuniaoResponse atualizar(@PathParam("id") long id, ReuniaoRequest request) {
        return reuniaoService.atualizar(id, request);
    }

    @PATCH
    @Path("/{id}/status")
    public ReuniaoResponse alterarStatus(@PathParam("id") long id, StatusRequest request) {
        if (request == null) {
            throw new NegocioException("Informe o novo status da reunião.");
        }
        return reuniaoService.alterarStatus(id, request.status());
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") long id) {
        reuniaoService.excluir(id);
        return Response.noContent().build();
    }

    /** Envia (ou substitui) a transcricao e devolve a analise gerada. */
    @POST
    @Path("/{id}/transcricao")
    public Response enviarTranscricao(@PathParam("id") long id, TranscricaoRequest request, @Context UriInfo uriInfo) {
        AnaliseReuniao analise = analiseService.enviarTranscricao(id, request == null ? null : request.texto());
        return Response.created(uriInfo.getBaseUriBuilder().path("reunioes").path(String.valueOf(id)).path("analise").build())
                .entity(analise)
                .build();
    }

    @GET
    @Path("/{id}/transcricao")
    public Transcricao buscarTranscricao(@PathParam("id") long id) {
        return analiseService.buscarTranscricao(id);
    }

    @GET
    @Path("/{id}/analise")
    public AnaliseReuniao buscarAnalise(@PathParam("id") long id) {
        return analiseService.buscarAnalise(id);
    }
}
