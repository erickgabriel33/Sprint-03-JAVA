package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record TranscricaoDTO(
        @NotBlank
        @Size(min = 20, max = 4000)
        String texto
) {
}
