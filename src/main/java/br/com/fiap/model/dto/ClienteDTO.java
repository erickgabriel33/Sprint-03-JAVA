package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record ClienteDTO(
        @NotBlank
        @Size(min = 2, max = 120)
        String nome,
        @NotBlank
        @Pattern(regexp = "\\d{14}", message = "deve conter 14 números, sem pontos e traços")
        String cnpj,
        @NotBlank
        String segmento,
        @NotBlank
        @Pattern(regexp = "PEQUENO|MEDIO|GRANDE", message = "deve ser PEQUENO, MEDIO ou GRANDE")
        String porte,
        @Email
        String email
) {
}
