package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.ReuniaoResponse;
import br.com.fiap.reunia.model.Cliente;
import br.com.fiap.reunia.service.ClienteService;
import br.com.fiap.reunia.service.ReuniaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes")
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @Inject
    ReuniaoService reuniaoService;

    @GET
    public List<Cliente> listar(@QueryParam("nome") String nome) {
        return clienteService.listar(nome);
    }

    @GET
    @Path("/{id}")
    public Cliente buscar(@PathParam("id") long id) {
        return clienteService.buscarPorId(id);
    }

    @GET
    @Path("/{id}/reunioes")
    public List<ReuniaoResponse> reunioesDoCliente(@PathParam("id") long id) {
        clienteService.buscarPorId(id);
        return reuniaoService.listar(null, null, id, null);
    }

    @POST
    public Response cadastrar(Cliente cliente, @Context UriInfo uriInfo) {
        Cliente criado = clienteService.cadastrar(cliente);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(criado.getId())).build())
                .entity(criado)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Cliente atualizar(@PathParam("id") long id, Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") long id) {
        clienteService.excluir(id);
        return Response.noContent().build();
    }
}
