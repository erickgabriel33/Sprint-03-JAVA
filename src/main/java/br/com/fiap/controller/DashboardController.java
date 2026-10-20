package br.com.fiap.controller;

import br.com.fiap.model.dto.ClienteRiscoDTO;
import br.com.fiap.model.dto.DashboardDTO;
import br.com.fiap.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // libera o acesso da API para o site (front-end) do grupo
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> consultar() {
        DashboardDTO resumo = dashboardService.gerarResumo();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/clientes-risco")
    public ResponseEntity<List<ClienteRiscoDTO>> consultarClientesEmRisco() {
        List<ClienteRiscoDTO> clientes = dashboardService.listarClientesEmRisco();
        return ResponseEntity.ok(clientes);
    }
}
