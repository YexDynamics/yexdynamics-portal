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
        log.info("Registrando puntaje para el jugador: {} en el juego ID/Título: {} / {}",
                dto.getNickname(), dto.getGameId(), dto.getGameTitle());
        validateLeaderboardDTO(dto);

        // Find or create Player
        Player player = playerRepository.findByNickname(dto.getNickname())
                .orElseGet(() -> playerRepository.save(new Player(null, dto.getNickname())));

        // Find or create Game (se busca por ID, luego por Título, o se autogenera)
        Game game = resolveOrCreateGame(dto);

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

    private Game resolveOrCreateGame(LeaderboardDTO dto) {
        if (dto.getGameId() != null) {
            Game gameFound = gameRepository.findById(dto.getGameId()).orElse(null);
            if (gameFound != null) return gameFound;
        }

        if (dto.getGameTitle() != null && !dto.getGameTitle().trim().isEmpty()) {
            Game gameFound = gameRepository.findByTitle(dto.getGameTitle()).orElse(null);
            if (gameFound != null) return gameFound;
        }

        String fallbackTitle = (dto.getGameTitle() != null && !dto.getGameTitle().trim().isEmpty())
                ? dto.getGameTitle()
                : "Juego #" + (dto.getGameId() != null ? dto.getGameId() : "1");

        Game newGame = new Game();
        if (dto.getGameId() != null) {
            newGame.setId(dto.getGameId());
        }
        newGame.setTitle(fallbackTitle);
        newGame.setDescription("Registrado automáticamente al guardar puntaje");
        newGame.setVersion("1.0.0");

        return gameRepository.save(newGame);
    }

    private void validateLeaderboardDTO(LeaderboardDTO dto) {
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            throw new IllegalArgumentException("El nickname del jugador es obligatorio");
        }
        if (dto.getGameId() == null && (dto.getGameTitle() == null || dto.getGameTitle().trim().isEmpty())) {
            throw new IllegalArgumentException("Se requiere un ID de juego o un título de juego");
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