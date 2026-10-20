package br.com.fiap.controller;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.ClienteDTO;
import br.com.fiap.model.entity.Cliente;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.repository.ClienteRepository;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // libera o acesso da API para o site (front-end) do grupo
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid ClienteDTO clienteDTO) {
        try {
            Cliente cliente = clienteService.cadastrar(clienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar o cliente.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> consultar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarPorCodigo(@PathVariable Long codigo) {
        Cliente cliente = clienteRepository.findByCodigo(codigo);
        if (cliente != null) {
            return ResponseEntity.status(HttpStatus.OK).body(cliente);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
    }

    @GetMapping("/{codigo}/reunioes")
    public ResponseEntity<?> consultarReunioes(@PathVariable Long codigo) {
        if (clienteRepository.existsById(codigo)) {
            List<Reuniao> reunioes = reuniaoRepository.findByCodigoCliente(codigo);
            return ResponseEntity.ok(reunioes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<String> atualizar(@PathVariable Long codigo, @RequestBody @Valid ClienteDTO clienteDTO) {
        if (!clienteRepository.existsById(codigo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
        try {
            clienteService.atualizar(codigo, clienteDTO);
            return ResponseEntity.ok("Cliente atualizado com sucesso!");
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar o cliente.");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> excluir(@PathVariable Long codigo) {
        if (clienteRepository.existsById(codigo)) {
            try {
                clienteService.excluir(codigo);
                return ResponseEntity.ok("Cliente deletado com sucesso!");
            } catch (NegocioException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado");
        }
    }
}
