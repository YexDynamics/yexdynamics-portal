package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.AdminGameResponseDTO;
import com.yexdynamics.portal_indie.business.dto.GameStatusUpdateDTO;
import com.yexdynamics.portal_indie.business.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador REST para la moderación global de juegos. Exclusivo para administradores
 *
 * ENDPOINTS:
 * - GET    /api/v1/admin/games             - Listar todos los juegos de la plataforma, incluidos los inhabilitados
 * - PATCH  /api/v1/admin/games/{id}/status - Habilitar o deshabilitar cualquier juego de la plataforma
 * - DELETE /api/v1/admin/games/{id}        - Eliminar físicamente un juego (irreversible)
 */

@RestController
@RequestMapping("/api/v1/admin/games")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Games", description = "Moderación global del catálogo de juegos. Exclusivo para administradores")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminGameController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final GameService gameService;

    @GetMapping
    @Operation(
            summary = "Listar todos los juegos",
            description = "Retorna todos los juegos registrados en la plataforma, habilitados e inhabilitados, con su propietario"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de juegos obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminGameResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getAllGames() {
        log.debug("GET /api/v1/admin/games");

        try {
            return ResponseEntity.ok(gameService.getAllGamesForAdmin());

        } catch (Exception e) {
            log.error("Error al obtener la lista completa de juegos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cambiar el estado de un juego",
            description = "Habilita o deshabilita cualquier juego de la plataforma por motivos de moderación, sin importar su autor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateGameStatus(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody GameStatusUpdateDTO dto
    ) {
        log.warn("PATCH /api/v1/admin/games/{}/status - active={}", id, dto.getActive());

        try {
            gameService.updateGameStatus(id, dto.getActive());
            return ResponseEntity.ok("Estado del juego actualizado correctamente");

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al actualizar el estado del juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar juego permanentemente",
            description = "Elimina físicamente un juego de la base de datos. Exclusivo para administradores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
            @ApiResponse(responseCode = "409", description = "El juego tiene puntajes asociados y no puede eliminarse"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> hardDeleteGame(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.warn("DELETE /api/v1/admin/games/{} - Eliminación física solicitada", id);

        try {
            gameService.hardDeleteGame(id);
            return ResponseEntity.noContent().build();

        } catch (NoSuchElementException e) {
            log.warn("Juego no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            log.warn("No se pudo eliminar físicamente el juego ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al eliminar físicamente el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
