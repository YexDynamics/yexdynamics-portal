package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar el puntaje de un jugador desde un juego vinculado")
public class ScoreRequestDTO {

    @NotBlank(message = "El token del juego es obligatorio")
    @Schema(description = "Token de vinculación del juego", example = "3f2b8c1e-9d4a-4e7b-8a61-5c0d2f9b7a11", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gameToken;

    @Size(max = 50, message = "El nickname no puede superar los 50 caracteres")
    @Schema(description = "Nickname o username del jugador. Obligatorio cuando la petición no incluye un JWT", example = "AgenteIsa", maxLength = 50)
    private String nickname;

    @NotNull(message = "El puntaje es obligatorio")
    @PositiveOrZero(message = "El puntaje debe ser mayor o igual a 0")
    @Schema(description = "Puntaje obtenido en la partida", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer score;
}
