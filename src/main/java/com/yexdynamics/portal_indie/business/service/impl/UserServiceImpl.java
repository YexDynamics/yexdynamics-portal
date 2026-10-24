package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.AdminUserResponseDTO;
import com.yexdynamics.portal_indie.business.service.UserService;
import com.yexdynamics.portal_indie.persistence.entity.Role;
import com.yexdynamics.portal_indie.persistence.entity.User;
import com.yexdynamics.portal_indie.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "Usuario no encontrado con ID: ";
    private static final String NICKNAME_ALREADY_EXISTS = "El nickname ya está en uso: ";
    private static final String SELF_STATUS_CHANGE = "No puedes cambiar el estado de tu propia cuenta";
    private static final String SELF_ROLE_CHANGE = "No puedes cambiar el rol de tu propia cuenta";

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void claimNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        boolean nicknameTakenByAnotherUser = userRepository.existsByNicknameIgnoreCase(nickname)
                && (user.getNickname() == null || !user.getNickname().equalsIgnoreCase(nickname));

        if (nicknameTakenByAnotherUser) {
            throw new IllegalArgumentException(NICKNAME_ALREADY_EXISTS + nickname);
        }

        user.setNickname(nickname);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::mapToAdminDTO)
                .toList();
    }

    @Override
    @Transactional
    public AdminUserResponseDTO updateUserStatus(Long userId, boolean enabled, Long adminId) {
        if (userId.equals(adminId)) {
            throw new IllegalStateException(SELF_STATUS_CHANGE);
        }

        User user = findUser(userId);
        user.setEnabled(enabled);

        return mapToAdminDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public AdminUserResponseDTO updateUserRole(Long userId, Role role, Long adminId) {
        if (userId.equals(adminId)) {
            throw new IllegalStateException(SELF_ROLE_CHANGE);
        }

        User user = findUser(userId);
        user.setRole(role);

        return mapToAdminDTO(userRepository.save(user));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));
    }

    private AdminUserResponseDTO mapToAdminDTO(User user) {
        return new AdminUserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getRole().name(),
                Boolean.TRUE.equals(user.getEnabled()),
                user.getCreatedAt()
        );
    }
}
