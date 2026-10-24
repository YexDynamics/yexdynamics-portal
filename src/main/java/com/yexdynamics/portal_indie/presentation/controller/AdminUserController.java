package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.AdminUserResponseDTO;
import com.yexdynamics.portal_indie.business.dto.UserRoleUpdateDTO;
import com.yexdynamics.portal_indie.business.dto.UserStatusUpdateDTO;
import com.yexdynamics.portal_indie.business.service.UserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Controlador REST para la moderación global de usuarios. Exclusivo para administradores
 *
 * ENDPOINTS:
 * - GET   /api/v1/admin/users             - Listar todos los usuarios registrados
 * - PATCH /api/v1/admin/users/{id}/status - Habilitar o inhabilitar (banear) la cuenta de un usuario
 * - PATCH /api/v1/admin/users/{id}/role   - Actualizar el rol de un usuario
 */

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Users", description = "Moderación global de usuarios. Exclusivo para administradores")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final UserService userService;

    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna todos los usuarios registrados en la plataforma, habilitados e inhabilitados. Nunca incluye contraseñas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> getAllUsers() {
        log.debug("GET /api/v1/admin/users");

        try {
            return ResponseEntity.ok(userService.getAllUsers());

        } catch (Exception e) {
            log.error("Error al obtener la lista de usuarios", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Habilitar o inhabilitar la cuenta de un usuario",
            description = "Banea (enabled=false) o desbanea (enabled=true) a un usuario. Un administrador no puede cambiar el estado de su propia cuenta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El administrador intentó cambiar el estado de su propia cuenta"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateUserStatus(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDTO dto
    ) {
        log.warn("PATCH /api/v1/admin/users/{}/status - enabled={} (admin ID: {})", id, dto.getEnabled(), principal.getId());

        try {
            return ResponseEntity.ok(userService.updateUserStatus(id, dto.getEnabled(), principal.getId()));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            log.warn("Cambio de estado rechazado para el usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al actualizar el estado del usuario ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PatchMapping("/{id}/role")
    @Operation(
            summary = "Actualizar el rol de un usuario",
            description = "Asigna ROLE_USER o ROLE_ADMIN a un usuario. Un administrador no puede cambiar el rol de su propia cuenta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminUserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El administrador intentó cambiar el rol de su propia cuenta"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> updateUserRole(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO dto
    ) {
        log.warn("PATCH /api/v1/admin/users/{}/role - role={} (admin ID: {})", id, dto.getRole(), principal.getId());

        try {
            return ResponseEntity.ok(userService.updateUserRole(id, dto.getRole(), principal.getId()));

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            log.warn("Cambio de rol rechazado para el usuario ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al actualizar el rol del usuario ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
