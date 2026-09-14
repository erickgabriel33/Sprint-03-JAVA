package br.com.fiap.dao;

import br.com.fiap.exception.NegocioException;
import br.com.fiap.model.Usuario;
import java.util.List;

public interface UsuarioDAO {
    void salvar(Usuario usuario) throws NegocioException;
    List<Usuario> listarTodos() throws NegocioException;
    Usuario buscarPorId(int id) throws NegocioException;
    boolean atualizar(Usuario usuario) throws NegocioException;
    boolean excluir(int id) throws NegocioException;
}