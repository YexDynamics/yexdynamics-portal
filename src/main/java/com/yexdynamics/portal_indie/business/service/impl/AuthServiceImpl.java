package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.AuthResponseDTO;
import com.yexdynamics.portal_indie.business.dto.LoginRequestDTO;
import com.yexdynamics.portal_indie.business.dto.RegisterRequestDTO;
import com.yexdynamics.portal_indie.business.service.AuthService;
import com.yexdynamics.portal_indie.persistence.entity.Role;
import com.yexdynamics.portal_indie.persistence.entity.User;
import com.yexdynamics.portal_indie.persistence.repository.UserRepository;
import com.yexdynamics.portal_indie.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String USERNAME_ALREADY_EXISTS = "El nombre de usuario ya está en uso: ";
    private static final String EMAIL_ALREADY_EXISTS = "El correo ya está registrado: ";
    private static final String NICKNAME_ALREADY_EXISTS = "El nickname ya está en uso: ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException(USERNAME_ALREADY_EXISTS + dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXISTS + dto.getEmail());
        }
        if (dto.getNickname() != null && userRepository.existsByNicknameIgnoreCase(dto.getNickname())) {
            throw new IllegalArgumentException(NICKNAME_ALREADY_EXISTS + dto.getNickname());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        user = userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + dto.getUsername()));

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getNickname(), user.getRole().name());
    }
}
