package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.LoginRequest;
import br.com.fiap.reunia.dto.UsuarioRequest;
import br.com.fiap.reunia.dto.UsuarioResponse;
import br.com.fiap.reunia.service.UsuarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação")
public class AuthResource {

    @Inject
    UsuarioService usuarioService;

    @POST
    @Path("/login")
    public UsuarioResponse login(LoginRequest request) {
        return usuarioService.autenticar(request);
    }

    @POST
    @Path("/cadastro")
    public Response cadastro(UsuarioRequest request, @Context UriInfo uriInfo) {
        UsuarioResponse criado = usuarioService.cadastrar(request);
        return Response.created(uriInfo.getBaseUriBuilder().path("usuarios").path(String.valueOf(criado.id())).build())
                .entity(criado)
                .build();
    }
}
