package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta detallada de un puntaje registrado")
public class LeaderboardResponseDTO {

    @Schema(description = "ID único del registro de puntaje", example = "1")
    private Long id;

    @Schema(description = "Nickname del jugador", example = "AgenteIsa")
    private String nickname;

    @Schema(description = "Título del juego", example = "PuntoBall")
    private String gameTitle;

    @Schema(description = "Puntaje obtenido", example = "1500")
    private Integer scoreValue;

    @Schema(description = "Fecha y hora en que se logró el puntaje")
    private LocalDateTime achievedAt;
}