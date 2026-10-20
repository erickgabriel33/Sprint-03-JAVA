package br.com.fiap.controller;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.LoginDTO;
import br.com.fiap.model.dto.UsuarioDTO;
import br.com.fiap.model.dto.UsuarioRespostaDTO;
import br.com.fiap.model.entity.Reuniao;
import br.com.fiap.model.entity.Usuario;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.model.repository.UsuarioRepository;
import br.com.fiap.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*") // libera o acesso da API para o site (front-end) do grupo
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioService.cadastrar(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioRespostaDTO(usuario));
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar o usuário.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        Usuario usuario = usuarioService.login(loginDTO);
        if (usuario != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new UsuarioRespostaDTO(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioRespostaDTO>> consultar() {
        List<UsuarioRespostaDTO> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findAll()) {
            usuarios.add(new UsuarioRespostaDTO(usuario));
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarPorCodigo(@PathVariable Long codigo) {
        Usuario usuario = usuarioRepository.findByCodigo(codigo);
        if (usuario != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new UsuarioRespostaDTO(usuario));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }

    @GetMapping("/{codigo}/reunioes")
    public ResponseEntity<?> consultarReunioes(@PathVariable Long codigo) {
        if (usuarioRepository.existsById(codigo)) {
            List<Reuniao> reunioes = reuniaoRepository.findByCodigoUsuario(codigo);
            return ResponseEntity.ok(reunioes);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<String> atualizar(@PathVariable Long codigo, @RequestBody @Valid UsuarioDTO usuarioDTO) {
        if (!usuarioRepository.existsById(codigo)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        try {
            usuarioService.atualizar(codigo, usuarioDTO);
            return ResponseEntity.ok("Usuário atualizado com sucesso!");
        } catch (NegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar o usuário.");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> excluir(@PathVariable Long codigo) {
        if (usuarioRepository.existsById(codigo)) {
            try {
                usuarioService.excluir(codigo);
                return ResponseEntity.ok("Usuário deletado com sucesso!");
            } catch (NegocioException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }
}
