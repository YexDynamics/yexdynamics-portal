package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.CreateGameDTO;
import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.dto.UpdateGameDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Games", description = "Gestión del catálogo de juegos indie")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final GameService gameService;

    @PostMapping
    @Operation(
            summary = "Registrar juego",
            description = "Registra un nuevo juego indie en el catálogo del portal."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Juego registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o título repetido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> createGame(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del juego a registrar",
                    required = true
            )
            @Valid @RequestBody CreateGameDTO dto
    ) {
        log.info("POST /api/v1/games - Registrando nuevo juego: {}", dto.getTitle());

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(dto));

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al registrar juego: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al registrar el juego", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @GetMapping
    @Operation(
            summary = "Obtener catálogo de juegos",
            description = "Retorna la lista de juegos activos registrados en la plataforma"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de juegos obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getAllGames() {
        log.debug("GET /api/v1/games");

        try {
            return ResponseEntity.ok(gameService.getAllGames());

        } catch (Exception e) {
            log.error("Error al obtener la lista de juegos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener juego por ID",
            description = "Retorna el detalle completo de un juego activo según su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juego encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado o deshabilitado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getGameById(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.debug("GET /api/v1/games/{}", id);

        try {
            return ResponseEntity.ok(gameService.getGameById(id));

        } catch (NoSuchElementException e) {
            log.warn("Juego no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al buscar el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar juego",
            description = "Actualiza la información de un juego activo del catálogo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Juego actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o título repetido"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado o deshabilitado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateGame(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del juego",
                    required = true
            )
            @Valid @RequestBody UpdateGameDTO dto
    ) {
        log.info("PUT /api/v1/games/{}", id);

        try {
            return ResponseEntity.ok(gameService.updateGame(id, dto));

        } catch (NoSuchElementException e) {
            log.warn("Juego no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

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
            summary = "Deshabilitar juego",
            description = "Deshabilita un juego del catálogo (borrado lógico). El registro se conserva en la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego deshabilitado correctamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado o ya deshabilitado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> deleteGame(
            @Parameter(description = "ID del juego", required = true, example = "1")
            @PathVariable Long id
    ) {
        log.info("DELETE /api/v1/games/{}", id);

        try {
            gameService.deleteGame(id);
            return ResponseEntity.noContent().build();

        } catch (NoSuchElementException e) {
            log.warn("Juego no encontrado ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al deshabilitar el juego ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @DeleteMapping("/admin/{id}")
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
        log.warn("DELETE /api/v1/games/admin/{} - Eliminación física solicitada", id);

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