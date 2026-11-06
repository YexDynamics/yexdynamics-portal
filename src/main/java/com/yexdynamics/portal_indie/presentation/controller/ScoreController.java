package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.ScoreRequestDTO;
import com.yexdynamics.portal_indie.business.dto.ScoreResponseDTO;
import com.yexdynamics.portal_indie.business.service.ScoreService;
import com.yexdynamics.portal_indie.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador REST para el envío de puntajes desde juegos vinculados al leaderboard online
 *
 * ENDPOINTS:
 * - POST /api/v1/scores - Enviar el puntaje de un jugador usando el token del juego (JWT opcional)
 */

@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scores", description = "Envío de puntajes desde juegos vinculados al leaderboard online")
@CrossOrigin(origins = "http://localhost:4200")
public class ScoreController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final ScoreService scoreService;

    @PostMapping
    @Operation(
            summary = "Enviar puntaje",
            description = "Registra el puntaje de un jugador en un juego vinculado. Si hay JWT se usa el usuario autenticado; si no, se requiere el nickname. Solo se conserva el puntaje máximo por jugador y juego"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Puntaje procesado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScoreResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "La cuenta del jugador está suspendida o inhabilitada"),
            @ApiResponse(responseCode = "404", description = "Token de juego inválido o juego inhabilitado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> submitScore(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ScoreRequestDTO dto
    ) {
        log.info("POST /api/v1/scores - {}", principal != null ? "usuario ID: " + principal.getId() : "petición pública");

        try {
            return ResponseEntity.ok(scoreService.submitScore(dto, principal != null ? principal.getUser() : null));

        } catch (NoSuchElementException e) {
            log.warn("Token de juego inválido recibido en POST /api/v1/scores");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al registrar puntaje: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al registrar el puntaje", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
