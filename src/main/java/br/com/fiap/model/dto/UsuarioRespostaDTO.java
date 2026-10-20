package br.com.fiap.model.dto;

import br.com.fiap.model.entity.Usuario;

// Dados do usuario devolvidos pela API (sem a senha)
public record UsuarioRespostaDTO(
        Long codigo,
        String nome,
        String email,
        String cargo,
        String empresa
) {
    public UsuarioRespostaDTO(Usuario usuario) {
        this(usuario.getCodigo(), usuario.getNome(), usuario.getEmail(), usuario.getCargo(), usuario.getEmpresa());
    }
}
