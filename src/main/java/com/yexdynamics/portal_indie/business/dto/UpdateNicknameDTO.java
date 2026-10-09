package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para asociar o actualizar el nickname del Leaderboard vinculado a la cuenta")
public class UpdateNicknameDTO {

    @NotBlank(message = "El nickname es obligatorio")
    @Size(max = 50, message = "El nickname no puede superar los 50 caracteres")
    @Schema(description = "Nickname usado previamente en el Leaderboard", example = "AgenteIsa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
}
