package com.yexdynamics.portal_indie.presentation.controller;

import com.yexdynamics.portal_indie.business.dto.AuthResponseDTO;
import com.yexdynamics.portal_indie.business.dto.LoginRequestDTO;
import com.yexdynamics.portal_indie.business.dto.RegisterRequestDTO;
import com.yexdynamics.portal_indie.business.service.AuthService;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para registro, inicio de sesión y emisión de tokens JWT
 *
 * ENDPOINTS:
 * - POST /api/v1/auth/register - Registrar una nueva cuenta de usuario
 * - POST /api/v1/auth/login    - Iniciar sesión y obtener un token JWT
 */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Registro, inicio de sesión y emisión de tokens JWT")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private static final String INTERNAL_ERROR = "Error interno del servidor";

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuario",
            description = "Crea una nueva cuenta de usuario y retorna un token JWT para iniciar sesión automáticamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, usuario, correo o nickname ya registrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto) {
        log.info("POST /api/v1/auth/register - Registrando usuario: {}", dto.getUsername());

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));

        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al registrar usuario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("Error al registrar el usuario", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y retorna un token JWT válido por 24 horas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sesión iniciada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("POST /api/v1/auth/login - Usuario: {}", dto.getUsername());

        try {
            return ResponseEntity.ok(authService.login(dto));

        } catch (AuthenticationException e) {
            log.warn("Credenciales inválidas para el usuario: {}", dto.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");

        } catch (Exception e) {
            log.error("Error al iniciar sesión", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_ERROR);
        }
    }
}
