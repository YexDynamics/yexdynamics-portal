package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de un usuario para el panel de moderación. Nunca incluye la contraseña")
public class AdminUserResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "agente_isa")
    private String username;

    @Schema(description = "Nickname vinculado al Leaderboard (puede ser nulo)", example = "AgenteIsa")
    private String nickname;

    @Schema(description = "Correo electrónico", example = "isa@example.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "ROLE_USER")
    private String role;

    @Schema(description = "true si la cuenta está habilitada, false si fue baneada", example = "true")
    private Boolean enabled;

    @Schema(description = "Fecha y hora de registro de la cuenta", example = "2026-09-10T15:30:00")
    private LocalDateTime createdAt;
}
