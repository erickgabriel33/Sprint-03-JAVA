package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record LoginDTO(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String senha
) {
}
