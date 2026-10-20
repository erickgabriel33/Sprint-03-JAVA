package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record StatusDTO(
        @NotBlank
        @Pattern(regexp = "AGENDADA|REALIZADA|CANCELADA", message = "deve ser AGENDADA, REALIZADA ou CANCELADA")
        String status
) {
}
