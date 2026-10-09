package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para que un administrador cambie el estado de un juego (moderación)")
public class GameStatusUpdateDTO {

    @NotNull(message = "El estado 'active' es obligatorio")
    @Schema(description = "true para habilitar el juego, false para deshabilitarlo", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean active;
}
