package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar un nuevo puntaje")
public class LeaderboardDTO {

    @Schema(description = "ID único del registro (sólo lectura)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nickname o apodo del jugador", example = "AgenteIsa", required = true, maxLength = 50)
    private String nickname;

    @Schema(description = "ID del juego asociado (opcional si se envía gameTitle)", example = "1")
    private Long gameId;

    @Schema(description = "Título del juego (opcional, para crearlo si no existe)", example = "PuntoBall")
    private String gameTitle;

    @Schema(description = "Valor del puntaje alcanzado", example = "1500", required = true)
    private Integer scoreValue;
}