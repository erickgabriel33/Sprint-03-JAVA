package br.com.fiap.reunia.service;

import br.com.fiap.reunia.dao.ReuniaoDAO;
import br.com.fiap.reunia.dao.UsuarioDAO;
import br.com.fiap.reunia.dto.LoginRequest;
import br.com.fiap.reunia.dto.UsuarioRequest;
import br.com.fiap.reunia.dto.UsuarioResponse;
import br.com.fiap.reunia.exception.AutenticacaoException;
import br.com.fiap.reunia.exception.ConflitoException;
import br.com.fiap.reunia.exception.NegocioException;
import br.com.fiap.reunia.exception.RecursoNaoEncontradoException;
import br.com.fiap.reunia.model.Usuario;
import br.com.fiap.reunia.util.SenhaUtil;
import br.com.fiap.reunia.util.ValidadorUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static br.com.fiap.reunia.factory.GerenciadorTransacao.consultar;
import static br.com.fiap.reunia.factory.GerenciadorTransacao.executarEmTransacao;

/**
 * Regras e validacoes de usuarios, incluindo cadastro e login.
 */
@ApplicationScoped
public class UsuarioService {

    public List<UsuarioResponse> listar() {
        return consultar(con -> new UsuarioDAO(con).listar()).stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    public UsuarioResponse buscarPorId(long id) {
        return UsuarioResponse.de(consultar(con -> new UsuarioDAO(con).buscarPorId(id))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id)));
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        validarCorpo(request);
        Usuario usuario = montar(request, new Usuario());
        ValidadorUtil.senhaForte(request.senha());
        usuario.setSenhaHash(SenhaUtil.gerarHash(request.senha()));

        return UsuarioResponse.de(executarEmTransacao(con -> {
            UsuarioDAO dao = new UsuarioDAO(con);
            if (dao.existeEmail(usuario.getEmail(), null)) {
                throw new ConflitoException("Já existe um usuário cadastrado com o e-mail " + usuario.getEmail() + ".");
            }
            return dao.inserir(usuario);
        }));
    }

    public UsuarioResponse atualizar(long id, UsuarioRequest request) {
        validarCorpo(request);
        return UsuarioResponse.de(executarEmTransacao(con -> {
            UsuarioDAO dao = new UsuarioDAO(con);
            Usuario usuario = dao.buscarPorId(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
            montar(request, usuario);
            if (request.senha() != null && !request.senha().isBlank()) {
                ValidadorUtil.senhaForte(request.senha());
                usuario.setSenhaHash(SenhaUtil.gerarHash(request.senha()));
            }
            if (dao.existeEmail(usuario.getEmail(), id)) {
                throw new ConflitoException("O e-mail " + usuario.getEmail() + " já pertence a outro usuário.");
            }
            dao.atualizar(usuario);
            return usuario;
        }));
    }

    public void excluir(long id) {
        executarEmTransacao(con -> {
            if (new ReuniaoDAO(con).contarPorUsuario(id) > 0) {
                throw new ConflitoException("Usuário possui reuniões vinculadas. Transfira ou exclua as reuniões antes.");
            }
            if (!new UsuarioDAO(con).excluir(id)) {
                throw new RecursoNaoEncontradoException("Usuário", id);
            }
            return null;
        });
    }

    /** Login simples: confere e-mail e senha (hash). Mensagem generica para nao revelar qual dado esta errado. */
    public UsuarioResponse autenticar(LoginRequest request) {
        if (request == null || request.email() == null || request.senha() == null) {
            throw new NegocioException("Informe e-mail e senha.");
        }
        Usuario usuario = consultar(con -> new UsuarioDAO(con).buscarPorEmail(request.email().trim()))
                .filter(u -> SenhaUtil.confere(request.senha(), u.getSenhaHash()))
                .orElseThrow(() -> new AutenticacaoException("E-mail ou senha inválidos."));
        return UsuarioResponse.de(usuario);
    }

    private void validarCorpo(UsuarioRequest request) {
        if (request == null) {
            throw new NegocioException("Corpo da requisição é obrigatório.");
        }
    }

    private Usuario montar(UsuarioRequest request, Usuario usuario) {
        usuario.setNome(ValidadorUtil.textoObrigatorio(request.nome(), "nome", 3, 100));
        usuario.setEmail(ValidadorUtil.email(request.email(), "email", true));
        usuario.setCargo(ValidadorUtil.textoOpcional(request.cargo(), "cargo", 60));
        usuario.setEmpresa(ValidadorUtil.textoOpcional(request.empresa(), "empresa", 80));
        return usuario;
    }
}
