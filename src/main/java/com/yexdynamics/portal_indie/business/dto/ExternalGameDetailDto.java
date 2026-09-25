package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de detalle de un juego indie externo obtenido desde RAWG")
public class ExternalGameDetailDto {

    @Schema(description = "ID del juego en RAWG", example = "3498")
    private Long id;

    @Schema(description = "Título del juego", example = "Hollow Knight")
    private String title;

    @Schema(description = "Fecha de lanzamiento", example = "2017-02-24")
    private String releaseDate;

    @Schema(description = "URL de la imagen de portada", example = "https://media.rawg.io/media/games/4cf/4cfc6b7f1850590a4634b08bfab308ab.jpg")
    private String coverImageUrl;

    @Schema(description = "Calificación promedio en RAWG", example = "4.4")
    private Double rating;

    @Schema(description = "Géneros del juego", example = "[\"Action\", \"Adventure\"]")
    private List<String> genres;

    @Schema(description = "Plataformas donde está disponible", example = "[\"PC\", \"Nintendo Switch\"]")
    private List<String> platforms;

    @Schema(description = "Descripción completa del juego", example = "Forge your own path in Hollow Knight...")
    private String description;

    @Schema(description = "Sitio web oficial del juego", example = "https://www.hollowknight.com")
    private String website;

    @Schema(description = "Clasificación ESRB", example = "Everyone 10+")
    private String esrbRating;

    @Schema(description = "Links a las tiendas donde se puede comprar el juego", example = "[\"https://store.steampowered.com/app/367520\"]")
    private List<String> storeUrls;
}
