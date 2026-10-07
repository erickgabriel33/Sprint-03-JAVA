package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.ClienteRiscoResponse;
import br.com.fiap.reunia.dto.DashboardResponse;
import br.com.fiap.reunia.service.DashboardService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard")
public class DashboardResource {

    @Inject
    DashboardService dashboardService;

    @GET
    @Path("/resumo")
    public DashboardResponse resumo() {
        return dashboardService.resumo();
    }

    @GET
    @Path("/clientes-risco")
    public List<ClienteRiscoResponse> clientesEmRisco(@QueryParam("minimo") @DefaultValue("60") int minimo) {
        return dashboardService.clientesEmRisco(minimo);
    }
}
