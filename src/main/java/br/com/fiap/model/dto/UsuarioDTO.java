package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record UsuarioDTO(
        @NotBlank
        @Size(min = 3, max = 100)
        String nome,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 6, max = 30)
        String senha,
        String cargo,
        String empresa
) {
}
