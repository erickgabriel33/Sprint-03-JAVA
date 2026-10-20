package br.com.fiap.controller;

import br.com.fiap.model.dto.TranscricaoDTO;
import br.com.fiap.model.entity.Analise;
import br.com.fiap.service.AnaliseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // libera o acesso da API para o site (front-end) do grupo
@RestController
@RequestMapping("/analises")
public class AnaliseController {

    @Autowired
    private AnaliseService analiseService;

    // Analisa um texto sem salvar no banco (pre-visualizacao)
    @PostMapping("/simular")
    public ResponseEntity<Analise> simular(@RequestBody @Valid TranscricaoDTO transcricaoDTO) {
        Analise analise = analiseService.analisar(transcricaoDTO.texto());
        return ResponseEntity.ok(analise);
    }
}
