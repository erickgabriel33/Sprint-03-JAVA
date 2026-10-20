package br.com.fiap.service;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.dto.LoginDTO;
import br.com.fiap.model.dto.UsuarioDTO;
import br.com.fiap.model.entity.Usuario;
import br.com.fiap.model.repository.ReuniaoRepository;
import br.com.fiap.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    public Usuario cadastrar(UsuarioDTO usuarioDTO) throws NegocioException {
        if (usuarioRepository.findByEmail(usuarioDTO.email()) != null) {
            throw new NegocioException("Já existe um usuário cadastrado com o e-mail " + usuarioDTO.email() + ".");
        }
        Usuario usuario = new Usuario(usuarioDTO);
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(Long codigo, UsuarioDTO usuarioDTO) throws NegocioException {
        Usuario usuarioComMesmoEmail = usuarioRepository.findByEmail(usuarioDTO.email());
        if (usuarioComMesmoEmail != null && !usuarioComMesmoEmail.getCodigo().equals(codigo)) {
            throw new NegocioException("O e-mail " + usuarioDTO.email() + " já pertence a outro usuário.");
        }
        Usuario usuario = new Usuario(usuarioDTO);
        usuario.setCodigo(codigo);
        return usuarioRepository.save(usuario);
    }

    public void excluir(Long codigo) throws NegocioException {
        if (!reuniaoRepository.findByCodigoUsuario(codigo).isEmpty()) {
            throw new NegocioException("O usuário possui reuniões cadastradas e não pode ser excluído.");
        }
        usuarioRepository.deleteById(codigo);
    }

    // Retorna o usuario quando e-mail e senha conferem, ou null quando o login e invalido
    public Usuario login(LoginDTO loginDTO) {
        return usuarioRepository.findByEmailAndSenha(loginDTO.email(), loginDTO.senha());
    }
}
