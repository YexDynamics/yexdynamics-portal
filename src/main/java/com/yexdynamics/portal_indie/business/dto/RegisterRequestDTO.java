package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar una nueva cuenta de usuario")
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "agente_isa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Schema(description = "Correo electrónico único", example = "isa@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    @Schema(description = "Contraseña de la cuenta", example = "superClaveSegura123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Size(max = 50, message = "El nickname no puede superar los 50 caracteres")
    @Schema(description = "Nickname para vincular los puntajes del Leaderboard (opcional, distinto del nickname interno del juego)", example = "AgenteIsa")
    private String nickname;
}
