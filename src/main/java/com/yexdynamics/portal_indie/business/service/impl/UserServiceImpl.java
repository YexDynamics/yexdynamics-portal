package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.service.UserService;
import com.yexdynamics.portal_indie.persistence.entity.User;
import com.yexdynamics.portal_indie.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "Usuario no encontrado con ID: ";
    private static final String NICKNAME_ALREADY_EXISTS = "El nickname ya está en uso: ";

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
}
