package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.dto.UpdateGameDTO;
import com.yexdynamics.portal_indie.business.service.GameService;
import com.yexdynamics.portal_indie.security.CustomUserDetails;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión personal de juegos del usuario autenticado
 *
 * ENDPOINTS:
 * - GET    /api/v1/my-games      - Listar mis juegos (activos e inhabilitados)
 * - GET    /api/v1/my-games/{id} - Obtener el detalle de uno de mis juegos
 * - PUT    /api/v1/my-games/{id} - Actualizar uno de mis juegos
 * - DELETE /api/v1/my-games/{id} - Deshabilitar uno de mis juegos (borrado lógico)
 */

@RestController
@RequestMapping("/api/v1/my-games")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "My Games", description = "Gestión personal de los juegos publicados por el usuario autenticado")
@CrossOrigin(origins = "http://localhost:4200")
public class MyGameController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final GameService gameService;

    @GetMapping
    @Operation(
            summary = "Listar mis juegos",
            description = "Retorna todos los juegos publicados por el usuario autenticado, activos e inhabilitados"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getMyGames(@AuthenticationPrincipal CustomUserDetails principal) {
        log.debug("GET /api/v1/my-games - usuario ID: {}", principal.getId());

        try {
            return ResponseEntity.ok(gameService.getMyGames(principal.getId()));

        } catch (Exception e) {
            log.error("Error al obtener los juegos del usuario ID {}", principal.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener uno de mis juegos",
            description = "Retorna el detalle de un juego propio, esté activo o inhabilitado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juego encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(responseCode = "403", description = "El juego no pertenece al usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getMyGameById(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.debug("GET /api/v1/my-games/{} - usuario ID: {}", id, principal.getId());

        try {
            return ResponseEntity.ok(gameService.getMyGameById(id, principal.getId()));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al buscar el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar uno de mis juegos",
            description = "Actualiza la información de un juego propio. Solo el autor puede editar sus datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juego actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o título repetido"),
            @ApiResponse(responseCode = "403", description = "El juego no pertenece al usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateMyGame(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateGameDTO dto
    ) {
        log.info("PUT /api/v1/my-games/{} - usuario ID: {}", id, principal.getId());

        try {
            return ResponseEntity.ok(gameService.updateMyGame(id, dto, principal.getId()));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al actualizar juego ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al actualizar el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deshabilitar uno de mis juegos",
            description = "Deshabilita (borrado lógico) un juego propio. El registro se conserva en la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego deshabilitado correctamente"),
            @ApiResponse(responseCode = "403", description = "El juego no pertenece al usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> disableMyGame(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/my-games/{} - usuario ID: {}", id, principal.getId());

        try {
            gameService.disableMyGame(id, principal.getId());
            return ResponseEntity.noContent().build();

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al deshabilitar el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
