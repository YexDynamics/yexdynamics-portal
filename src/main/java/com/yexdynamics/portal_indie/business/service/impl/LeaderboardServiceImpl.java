package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.LeaderboardDTO;
import com.yexdynamics.portal_indie.business.dto.LeaderboardResponseDTO;
import com.yexdynamics.portal_indie.business.service.LeaderboardService;
import com.yexdynamics.portal_indie.persistence.entity.Game;
import com.yexdynamics.portal_indie.persistence.entity.Leaderboard;
import com.yexdynamics.portal_indie.persistence.entity.Player;
import com.yexdynamics.portal_indie.persistence.repository.GameRepository;
import com.yexdynamics.portal_indie.persistence.repository.LeaderboardRepository;
import com.yexdynamics.portal_indie.persistence.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Override
    public LeaderboardResponseDTO createScore(LeaderboardDTO dto) {
        log.info("Registrando puntaje para el jugador: {} en el juego ID: {}", dto.getNickname(), dto.getGameId());
        validateLeaderboardDTO(dto);

        Player player = playerRepository.findByNickname(dto.getNickname())
                .orElseGet(() -> playerRepository.save(new Player(null, dto.getNickname())));

        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new RuntimeException("Juego no encontrado con ID: " + dto.getGameId()));

        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setPlayer(player);
        leaderboard.setGame(game);
        leaderboard.setScoreValue(dto.getScoreValue());

        Leaderboard saved = leaderboardRepository.save(leaderboard);

        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardResponseDTO> getTopScoresByGame(Long gameId) {
        log.info("Obteniendo top scores para juego ID: {}", gameId);
        return leaderboardRepository.findByGameIdOrderByScoreValueDesc(gameId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LeaderboardResponseDTO getScoreById(Long id) {
        Leaderboard leaderboard = leaderboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de puntaje no encontrado con ID: " + id));
        return mapToResponseDTO(leaderboard);
    }

    @Override
    public void deleteScore(Long id) {
        log.info("Eliminando registro de puntaje ID: {}", id);
        leaderboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado con ID: " + id));
        leaderboardRepository.deleteById(id);
    }

    private void validateLeaderboardDTO(LeaderboardDTO dto) {
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            throw new IllegalArgumentException("El nickname del jugador es obligatorio");
        }
        if (dto.getGameId() == null) {
            throw new IllegalArgumentException("El ID del juego es obligatorio");
        }
        if (dto.getScoreValue() == null || dto.getScoreValue() < 0) {
            throw new IllegalArgumentException("El puntaje debe ser un valor válido mayor o igual a 0");
        }
    }

    private LeaderboardResponseDTO mapToResponseDTO(Leaderboard entity) {
        LeaderboardResponseDTO response = new LeaderboardResponseDTO();
        response.setId(entity.getId());
        response.setNickname(entity.getPlayer().getNickname());
        response.setGameTitle(entity.getGame().getTitle());
        response.setScoreValue(entity.getScoreValue());
        response.setAchievedAt(entity.getAchievedAt());
        return response;
    }
}