package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

// Campos de VENDAS: valorOportunidade, scoreRisco e concorrente
// Campos de CS: nps, reclamacoes e sinalChurn
public record ReuniaoDTO(
        @NotBlank
        @Size(min = 3, max = 150)
        String titulo,
        @NotNull
        LocalDateTime dataHora,
        @NotNull
        @Min(1)
        @Max(1440)
        Integer duracaoMinutos,
        @NotBlank
        @Pattern(regexp = "VENDAS|CS", message = "deve ser VENDAS ou CS")
        String tipo,
        @NotNull
        Long codigoUsuario,
        @NotNull
        Long codigoCliente,
        @PositiveOrZero
        Double valorOportunidade,
        @Min(0)
        @Max(100)
        Integer scoreRisco,
        Boolean concorrente,
        @Min(0)
        @Max(10)
        Integer nps,
        @PositiveOrZero
        Integer reclamacoes,
        Boolean sinalChurn
) {
}
