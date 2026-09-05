package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.LeaderboardDTO;
import com.yexdynamics.portal_indie.business.dto.LeaderboardResponseDTO;
import com.yexdynamics.portal_indie.business.service.LeaderboardService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para la gestión de la tabla de posiciones (Leaderboard)
 *
 * ENDPOINTS:
 * - POST   /api/v1/leaderboard             - Registrar un nuevo puntaje
 * - GET    /api/v1/leaderboard/game/{gameId} - Obtener el Top Scores de un juego
 * - GET    /api/v1/leaderboard/{id}        - Obtener registro por ID
 * - DELETE /api/v1/leaderboard/{id}        - Eliminar un registro de puntaje
 */

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leaderboard", description = "Gestión de tabla de clasificación y puntajes de juegos indie")
@CrossOrigin(origins = "http://localhost:4200")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * CREATE - Registrar nuevo puntaje
     */
    @PostMapping
    @Operation(
            summary = "Registrar puntaje",
            description = "Registra un nuevo puntaje para un jugador en un juego específico. Si el jugador no existe, lo crea automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Puntaje registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LeaderboardResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes"),
            @ApiResponse(responseCode = "404", description = "El juego especificado no fue encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> createScore(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del puntaje a registrar",
                    required = true
            )
            @RequestBody LeaderboardDTO dto
    ) {
        log.info("POST /api/v1/leaderboard");

        try {
            LeaderboardResponseDTO created = leaderboardService.createScore(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al registrar puntaje: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RuntimeException e) {
            log.warn("Recurso no encontrado o error de negocio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al registrar el puntaje: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    /**
     * READ ALL BY GAME - Obtener Top Scores de un juego
     */
    @GetMapping("/game/{gameId}")
    @Operation(
            summary = "Obtener Top Scores por juego",
            description = "Retorna la lista de puntajes ordenados de mayor a menor para un juego específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de puntajes obtenida exitosamente (puede ser un arreglo vacío)"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<?> getTopScoresByGame(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long gameId
    ) {
        log.debug("GET /api/v1/leaderboard/game/{}", gameId);

        try {
            List<LeaderboardResponseDTO> scores = leaderboardService.getTopScoresByGame(gameId);

            if (scores == null || scores.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(scores);

        } catch (Exception e) {
            log.error("Error al obtener puntajes del juego ID {}: {}", gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    /**
     * READ - Obtener registro por ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener puntaje por ID",
            description = "Retorna un registro de puntaje específico según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro de puntaje encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LeaderboardResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Registro de puntaje no encontrado")
    })
    public ResponseEntity<?> getScoreById(
            @Parameter(description = "ID del registro de puntaje", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.debug("GET /api/v1/leaderboard/{}", id);

        try {
            return ResponseEntity.ok(leaderboardService.getScoreById(id));

        } catch (RuntimeException e) {
            log.warn("Puntaje no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * DELETE - Eliminar puntaje
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar registro de puntaje",
            description = "Elimina de forma permanente un registro de la tabla de posiciones"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Puntaje eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Puntaje no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al eliminar")
    })
    public ResponseEntity<?> deleteScore(
            @Parameter(description = "ID del puntaje a eliminar", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/leaderboard/{}", id);

        try {
            leaderboardService.deleteScore(id);
            return ResponseEntity.ok("Puntaje eliminado correctamente");

        } catch (RuntimeException e) {
            log.warn("Puntaje no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error eliminando puntaje ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}