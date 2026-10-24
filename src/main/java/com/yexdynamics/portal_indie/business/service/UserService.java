package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.AdminUserResponseDTO;
import com.yexdynamics.portal_indie.persistence.entity.Role;

import java.util.List;

public interface UserService {
    void claimNickname(Long userId, String nickname);

    List<AdminUserResponseDTO> getAllUsers();
    AdminUserResponseDTO updateUserStatus(Long userId, boolean enabled, Long adminId);
    AdminUserResponseDTO updateUserRole(Long userId, Role role, Long adminId);
}
