package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.ExternalGameDetailDto;
import com.yexdynamics.portal_indie.business.dto.ExternalGameResponseDto;
import com.yexdynamics.portal_indie.business.service.ExternalGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

/**
 * Controlador REST para el catálogo de juegos indie externos (proxy de RAWG)
 *
 * ENDPOINTS:
 * - GET /api/v1/external-games       - Obtener juegos indie populares desde RAWG
 * - GET /api/v1/external-games/{id}  - Obtener el detalle de un juego externo por ID
 */

@RestController
@RequestMapping("/api/v1/external-games")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "External Games", description = "Catálogo de juegos indie externos obtenido desde RAWG")
@CrossOrigin(origins = "http://localhost:4200")
public class ExternalGameController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final ExternalGameService externalGameService;

    /**
     * READ ALL - Obtener juegos indie populares desde RAWG
     */
    @GetMapping
    @Operation(
            summary = "Obtener catálogo externo de juegos indie",
            description = "Consulta RAWG y retorna los juegos indie mejor calificados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de juegos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExternalGameResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getPopularIndieGames(
            @Parameter(description = "Cantidad máxima de juegos a retornar", example = "20")
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        log.debug("GET /api/v1/external-games?limit={}", limit);

        try {
            return ResponseEntity.ok(externalGameService.getPopularIndieGames(limit));

        } catch (Exception e) {
            log.error("Error al consultar el catálogo externo de RAWG", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    /**
     * READ - Obtener juego externo por ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener detalle de un juego externo",
            description = "Consulta RAWG y retorna el detalle completo de un juego indie por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juego encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExternalGameDetailDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado en RAWG"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getExternalGameById(
            @Parameter(description = "ID del juego en RAWG", required = true, example = "3498")
            @PathVariable Long id
    ) {
        log.debug("GET /api/v1/external-games/{}", id);

        try {
            return ResponseEntity.ok(externalGameService.getExternalGameById(id));

        } catch (NoSuchElementException e) {
            log.warn("Juego externo no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al consultar el juego externo ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
