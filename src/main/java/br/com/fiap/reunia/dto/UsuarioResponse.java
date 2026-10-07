package br.com.fiap.reunia.dto;

import br.com.fiap.reunia.model.Usuario;

import java.time.LocalDateTime;

/**
 * Usuario exposto pela API, sem o hash da senha.
 */
public record UsuarioResponse(Long id, String nome, String email, String cargo, String empresa, LocalDateTime dataCadastro) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getCargo(),
                usuario.getEmpresa(), usuario.getDataCadastro());
    }
}
