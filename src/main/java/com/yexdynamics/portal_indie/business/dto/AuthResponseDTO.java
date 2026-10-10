package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con el token JWT y los datos básicos del usuario")
public class AuthResponseDTO {

    @Schema(description = "Token JWT, válido por 24 horas", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Nombre de usuario", example = "Yeliel")
    private String username;

    @Schema(description = "Correo electrónico", example = "usuario@example.com")
    private String email;

    @Schema(description = "Nickname vinculado al Leaderboard (puede ser nulo si aún no lo ha definido)", example = "Yelion")
    private String nickname;

    @Schema(description = "Rol del usuario", example = "ROLE_USER")
    private String role;
}
