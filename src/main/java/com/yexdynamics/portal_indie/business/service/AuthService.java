package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.AuthResponseDTO;
import com.yexdynamics.portal_indie.business.dto.LoginRequestDTO;
import com.yexdynamics.portal_indie.business.dto.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO registerRequestDTO);
    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
