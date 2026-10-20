package br.com.fiap.controller;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ReuniaoDTO;
import br.com.fiap.model.dto.StatusDTO;
import br.com.fiap.model.dto.TranscricaoDTO;
import br.com.fiap.model.entity.Analise;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.entity.Transcricao;
import br.com.fiap.model.repository.AnaliseRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.model.repository.TranscricaoRepository;
import br.com.fiap.service.ReuniaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // libera o acesso da API para o site (front-end) do grupo
@RestController
@RequestMapping("/reunioes")
public class ReuniaoController {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private TranscricaoRepository transcricaoRepository;

    @Autowired
    private AnaliseRepository analiseRepository;

    @Autowired
    private ReuniaoService reuniaoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid ReuniaoDTO reuniaoDTO) {
        try {
            Reuniao reuniao = reuniaoService.cadastrar(reuniaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(reuniao);
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar a reunião.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Reuniao>> consultar() {
        List<Reuniao> reunioes = reuniaoRepository.findAll();
        return ResponseEntity.ok(reunioes);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarPorCodigo(@PathVariable Long codigo) {
        Reuniao reuniao = reuniaoRepository.findByCodigo(codigo);
        if (reuniao != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reuniao);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reunião não encontrada");
        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Reuniao>> consultarPorTipo(@PathVariable String tipo) {
        List<Reuniao> reunioes = reuniaoRepository.findByTipo(tipo.toUpperCase());
        return ResponseEntity.ok(reunioes);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reuniao>> consultarPorStatus(@PathVariable String status) {
        List<Reuniao> reunioes = reuniaoRepository.findByStatus(status.toUpperCase());
        return ResponseEntity.ok(reunioes);
    }

    @GetMapping("/prioritarias")
    public ResponseEntity<List<Reuniao>> consultarPrioritarias() {
        List<Reuniao> reunioes = reuniaoService.listarPrioritarias();
        return ResponseEntity.ok(reunioes);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<String> atualizar(@PathVariable Long codigo, @RequestBody @Valid ReuniaoDTO reuniaoDTO) {
        if (!reuniaoRepository.existsById(codigo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reunião não encontrada");
        }
        try {
            reuniaoService.atualizar(codigo, reuniaoDTO);
            return ResponseEntity.ok("Reunião atualizada com sucesso!");
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar a reunião.");
        }
    }

    @PutMapping("/{codigo}/status")
    public ResponseEntity<String> alterarStatus(@PathVariable Long codigo, @RequestBody @Valid StatusDTO statusDTO) {
        if (!reuniaoRepository.existsById(codigo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reunião não encontrada");
        }
        try {
            reuniaoService.alterarStatus(codigo, statusDTO.status());
            return ResponseEntity.ok("Status alterado para " + statusDTO.status() + " com sucesso!");
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> excluir(@PathVariable Long codigo) {
        if (reuniaoRepository.existsById(codigo)) {
            reuniaoService.excluir(codigo);
            return ResponseEntity.ok("Reunião deletada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reunião não encontrada");
        }
    }

    // Envia o texto da reuniao e devolve a analise gerada
    @PostMapping("/{codigo}/transcricao")
    public ResponseEntity<?> enviarTranscricao(@PathVariable Long codigo, @RequestBody @Valid TranscricaoDTO transcricaoDTO) {
        if (!reuniaoRepository.existsById(codigo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reunião não encontrada");
        }
        try {
            Analise analise = reuniaoService.enviarTranscricao(codigo, transcricaoDTO.texto());
            return ResponseEntity.status(HttpStatus.CREATED).body(analise);
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar a transcrição.");
        }
    }

    @GetMapping("/{codigo}/transcricao")
    public ResponseEntity<?> consultarTranscricao(@PathVariable Long codigo) {
        Transcricao transcricao = transcricaoRepository.findByCodigoReuniao(codigo);
        if (transcricao != null) {
            return ResponseEntity.status(HttpStatus.OK).body(transcricao);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transcrição não encontrada");
        }
    }

    @GetMapping("/{codigo}/analise")
    public ResponseEntity<?> consultarAnalise(@PathVariable Long codigo) {
        Analise analise = analiseRepository.findByCodigoReuniao(codigo);
        if (analise != null) {
            return ResponseEntity.status(HttpStatus.OK).body(analise);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Análise não encontrada");
        }
    }
}
