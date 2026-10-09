package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.UpdateNicknameDTO;
import com.yexdynamics.portal_indie.business.service.LeaderboardService;
import com.yexdynamics.portal_indie.business.service.UserService;
import com.yexdynamics.portal_indie.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Controlador REST para el perfil del usuario autenticado y la vinculación con el Leaderboard
 *
 * ENDPOINTS:
 * - PUT /api/v1/users/me/nickname - Asociar o actualizar el nickname vinculado al Leaderboard
 * - GET /api/v1/users/me/scores   - Obtener los puntajes registrados bajo el nickname vinculado
 */

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Perfil del usuario autenticado y vinculación con el Leaderboard")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final UserService userService;
    private final LeaderboardService leaderboardService;

    @PutMapping("/nickname")
    @Operation(
            summary = "Asociar nickname del Leaderboard",
            description = "Vincula o actualiza el nickname con el que el usuario registró puntajes previamente. " +
                    "Es independiente del nombre de usuario de la plataforma y del nickname interno del juego (máx. 8 caracteres)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nickname actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "El nickname ya está en uso"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateNickname(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UpdateNicknameDTO dto
    ) {
        log.info("PUT /api/v1/users/me/nickname - usuario ID: {}", principal.getId());

        try {
            userService.claimNickname(principal.getId(), dto.getNickname());
            return ResponseEntity.ok("Nickname actualizado correctamente");

        } catch (IllegalArgumentException e) {
            log.warn("No se pudo asociar el nickname: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al asociar el nickname", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @GetMapping("/scores")
    @Operation(
            summary = "Obtener mis puntajes del Leaderboard",
            description = "Retorna los puntajes registrados bajo el nickname vinculado a la cuenta autenticada"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente (vacía si aún no se ha vinculado un nickname)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getMyScores(@AuthenticationPrincipal CustomUserDetails principal) {
        log.debug("GET /api/v1/users/me/scores - usuario ID: {}", principal.getId());

        try {
            String nickname = principal.getNickname();

            if (nickname == null || nickname.isBlank()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(leaderboardService.getScoresByNickname(nickname));

        } catch (Exception e) {
            log.error("Error al obtener los puntajes del usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
