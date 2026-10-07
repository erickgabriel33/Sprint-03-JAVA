package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.ReuniaoResponse;
import br.com.fiap.reunia.dto.UsuarioRequest;
import br.com.fiap.reunia.dto.UsuarioResponse;
import br.com.fiap.reunia.service.ReuniaoService;
import br.com.fiap.reunia.service.UsuarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuários")
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    ReuniaoService reuniaoService;

    @GET
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GET
    @Path("/{id}")
    public UsuarioResponse buscar(@PathParam("id") long id) {
        return usuarioService.buscarPorId(id);
    }

    @GET
    @Path("/{id}/reunioes")
    public List<ReuniaoResponse> reunioesDoUsuario(@PathParam("id") long id) {
        usuarioService.buscarPorId(id);
        return reuniaoService.listar(null, null, null, id);
    }

    @POST
    public Response cadastrar(UsuarioRequest request, @Context UriInfo uriInfo) {
        UsuarioResponse criado = usuarioService.cadastrar(request);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(criado.id())).build())
                .entity(criado)
                .build();
    }

    @PUT
    @Path("/{id}")
    public UsuarioResponse atualizar(@PathParam("id") long id, UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") long id) {
        usuarioService.excluir(id);
        return Response.noContent().build();
    }
}
