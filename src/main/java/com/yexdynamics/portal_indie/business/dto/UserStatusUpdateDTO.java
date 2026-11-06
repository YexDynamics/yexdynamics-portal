package com.yexdynamics.portal_indie.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para que un administrador habilite o inhabilite la cuenta de un usuario")
public class UserStatusUpdateDTO {

    @NotNull(message = "El estado 'enabled' es obligatorio")
    @Schema(description = "true para habilitar la cuenta, false para inhabilitarla (banear)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;
}
