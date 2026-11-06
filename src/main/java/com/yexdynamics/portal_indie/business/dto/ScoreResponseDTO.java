package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado del envío de un puntaje")
public class ScoreResponseDTO {

    @Schema(description = "Título del juego", example = "PuntoBall")
    private String gameTitle;

    @Schema(description = "Nickname con el que aparece el jugador en el leaderboard", example = "AgenteIsa")
    private String nickname;

    @Schema(description = "Mejor puntaje del jugador en este juego", example = "1500")
    private Integer bestScore;

    @Schema(description = "Indica si el puntaje enviado superó el récord anterior", example = "true")
    private boolean newRecord;

    @Schema(description = "Fecha y hora en que se logró el mejor puntaje")
    private LocalDateTime achievedAt;
}
