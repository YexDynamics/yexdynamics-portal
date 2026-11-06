package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.ScoreRequestDTO;
import com.yexdynamics.portal_indie.business.dto.ScoreResponseDTO;
import com.yexdynamics.portal_indie.business.service.ScoreService;
import com.yexdynamics.portal_indie.persistence.entity.Game;
import com.yexdynamics.portal_indie.persistence.entity.Leaderboard;
import com.yexdynamics.portal_indie.persistence.entity.Player;
import com.yexdynamics.portal_indie.persistence.entity.User;
import com.yexdynamics.portal_indie.persistence.repository.GameRepository;
import com.yexdynamics.portal_indie.persistence.repository.LeaderboardRepository;
import com.yexdynamics.portal_indie.persistence.repository.PlayerRepository;
import com.yexdynamics.portal_indie.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private static final String INVALID_TOKEN = "El token del juego no es válido o el juego está inhabilitado";
    private static final String NICKNAME_REQUIRED = "El nickname es obligatorio cuando no hay una sesión autenticada";
    private static final String ACCOUNT_DISABLED = "La cuenta se encuentra suspendida o inhabilitada.";

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;

    @Override
    @Transactional
    public ScoreResponseDTO submitScore(ScoreRequestDTO dto, User user) {
        Game game = gameRepository.findByGameTokenAndActiveTrue(dto.getGameToken().trim())
                .orElseThrow(() -> new NoSuchElementException(INVALID_TOKEN));

        String nickname = resolvePlayerNickname(dto, user);
        Player player = playerRepository.findByNickname(nickname)
                .orElseGet(() -> playerRepository.save(new Player(null, nickname)));

        Leaderboard entry = leaderboardRepository.findByGameIdAndPlayerId(game.getId(), player.getId())
                .orElse(null);

        boolean newRecord = false;

        if (entry == null) {
            entry = new Leaderboard();
            entry.setGame(game);
            entry.setPlayer(player);
            entry.setScoreValue(dto.getScore());
            entry.setAchievedAt(LocalDateTime.now());
            entry = leaderboardRepository.save(entry);
            newRecord = true;
        } else if (dto.getScore() > entry.getScoreValue()) {
            entry.setScoreValue(dto.getScore());
            entry.setAchievedAt(LocalDateTime.now());
            entry = leaderboardRepository.save(entry);
            newRecord = true;
        }

        return new ScoreResponseDTO(
                game.getTitle(),
                player.getNickname(),
                entry.getScoreValue(),
                newRecord,
                entry.getAchievedAt()
        );
    }

    private String resolvePlayerNickname(ScoreRequestDTO dto, User authenticatedUser) {
        if (authenticatedUser != null) {
            return resolveNickname(authenticatedUser);
        }

        String requested = dto.getNickname() == null ? "" : dto.getNickname().trim();
        if (requested.isEmpty()) {
            throw new IllegalArgumentException(NICKNAME_REQUIRED);
        }

        User account = userRepository.findByNicknameIgnoreCase(requested)
                .or(() -> userRepository.findByUsername(requested))
                .orElse(null);

        if (account == null) {
            return requested;
        }
        if (!Boolean.TRUE.equals(account.getEnabled())) {
            throw new AccessDeniedException(ACCOUNT_DISABLED);
        }
        return resolveNickname(account);
    }

    private String resolveNickname(User user) {
        String nickname = user.getNickname();
        return (nickname != null && !nickname.isBlank()) ? nickname : user.getUsername();
    }
}
