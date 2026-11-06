package com.yexdynamics.portal_indie.business.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @JsonAlias("enabled")
    @NotNull(message = "El estado ('active' o 'enabled') es obligatorio")
    @Schema(description = "true para habilitar el juego, false para deshabilitarlo. También se acepta con el nombre 'enabled'", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean active;
}
