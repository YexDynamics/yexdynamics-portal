package com.yexdynamics.portal_indie.business.dto;

import com.yexdynamics.portal_indie.persistence.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para que un administrador actualice el rol de un usuario")
public class UserRoleUpdateDTO {

    @NotNull(message = "El rol es obligatorio")
    @Schema(description = "Nuevo rol del usuario", example = "ROLE_ADMIN", allowableValues = {"ROLE_USER", "ROLE_ADMIN"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private Role role;
}
