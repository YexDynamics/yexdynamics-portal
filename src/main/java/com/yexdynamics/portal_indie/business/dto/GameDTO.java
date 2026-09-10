package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta con la información completa de un juego")
public class GameDTO {

    @Schema(description = "ID único del juego", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Título del juego", example = "PuntoBall")
    private String title;

    @Schema(description = "Descripción o sinopsis del juego", example = "Un dinámico juego arcade de físicas y rebotes.")
    private String description;

    @Schema(description = "Versión actual del juego", example = "1.0.0")
    private String version;

    @Schema(description = "Género principal del juego", example = "Arcade")
    private String genre;

    @Schema(description = "Nombre del desarrollador o estudio creador", example = "YexDynamics")
    private String developerName;

    @Schema(description = "URL de la imagen de portada o banner", example = "https://example.com/images/puntoball.png")
    private String coverImageUrl;

    @Schema(description = "URL para descargar o jugar el título", example = "https://example.com/games/puntoball")
    private String downloadUrl;

    @Schema(description = "Fecha de registro en la plataforma", example = "2026-09-10T15:30:00")
    private LocalDateTime createdAt;
}