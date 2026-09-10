package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar un nuevo juego indie en la plataforma")
public class CreateGameDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres")
    @Schema(description = "Título del juego", example = "PuntoBall", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100)
    private String title;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Schema(description = "Descripción o sinopsis del juego", example = "Un dinámico juego arcade de físicas y rebotes.", maxLength = 500)
    private String description;

    @Size(max = 20, message = "La versión no puede superar los 20 caracteres")
    @Schema(description = "Versión del juego", example = "1.0.0", maxLength = 20)
    private String version;

    @Size(max = 50, message = "El género no puede superar los 50 caracteres")
    @Schema(description = "Género principal del juego", example = "Arcade", maxLength = 50)
    private String genre;

    @Size(max = 100, message = "El nombre del desarrollador no puede superar los 100 caracteres")
    @Schema(description = "Nombre del desarrollador o estudio creador", example = "YexDynamics", maxLength = 100)
    private String developerName;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    @Schema(description = "URL de la imagen de portada o banner", example = "https://example.com/images/puntoball.png", maxLength = 500)
    private String coverImageUrl;

    @Size(max = 500, message = "La URL de descarga no puede superar los 500 caracteres")
    @Schema(description = "URL para descargar o jugar el título", example = "https://example.com/games/puntoball", maxLength = 500)
    private String downloadUrl;
}