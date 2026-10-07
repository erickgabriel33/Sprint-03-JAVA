package br.com.fiap.reunia.resource;

import br.com.fiap.reunia.dto.TranscricaoRequest;
import br.com.fiap.reunia.model.AnaliseReuniao;
import br.com.fiap.reunia.service.AnaliseService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/analises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Análise de transcrição")
public class AnaliseResource {

    @Inject
    AnaliseService analiseService;

    /** Analisa um texto sem salvar no banco (pre-visualizacao). */
    @POST
    @Path("/simular")
    public AnaliseReuniao simular(TranscricaoRequest request) {
        return analiseService.simular(request == null ? null : request.texto());
    }
}
