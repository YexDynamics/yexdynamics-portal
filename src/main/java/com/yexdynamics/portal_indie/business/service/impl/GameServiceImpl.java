package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.CreateGameDTO;
import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.dto.UpdateGameDTO;
import com.yexdynamics.portal_indie.business.service.GameService;
import com.yexdynamics.portal_indie.persistence.entity.Game;
import com.yexdynamics.portal_indie.persistence.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private static final String DEFAULT_VERSION = "1.0.0";
    private static final String GAME_NOT_FOUND = "Juego no encontrado con ID: ";
    private static final String TITLE_ALREADY_EXISTS = "Ya existe un juego registrado con el título: ";

    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllGames() {
        return gameRepository.findAllByActiveTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        return mapToDTO(findActiveGame(id));
    }

    @Override
    @Transactional
    public GameDTO createGame(CreateGameDTO dto) {
        if (gameRepository.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException(TITLE_ALREADY_EXISTS + dto.getTitle());
        }

        Game game = new Game();
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setVersion(dto.getVersion() != null ? dto.getVersion() : DEFAULT_VERSION);
        game.setGenre(dto.getGenre());
        game.setDeveloperName(dto.getDeveloperName());
        game.setCoverImageUrl(dto.getCoverImageUrl());
        game.setDownloadUrl(dto.getDownloadUrl());

        return mapToDTO(gameRepository.save(game));
    }

    @Override
    @Transactional
    public GameDTO updateGame(Long id, UpdateGameDTO dto) {
        Game game = findActiveGame(id);

        if (gameRepository.existsByTitleAndIdNot(dto.getTitle(), id)) {
            throw new IllegalArgumentException(TITLE_ALREADY_EXISTS + dto.getTitle());
        }

        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setVersion(dto.getVersion() != null ? dto.getVersion() : game.getVersion());
        game.setGenre(dto.getGenre());
        game.setDeveloperName(dto.getDeveloperName());
        game.setCoverImageUrl(dto.getCoverImageUrl());
        game.setDownloadUrl(dto.getDownloadUrl());

        return mapToDTO(gameRepository.save(game));
    }

    @Override
    @Transactional
    public void deleteGame(Long id) {
        Game game = findActiveGame(id);
        game.setActive(false);
        gameRepository.save(game);
    }

    @Override
    @Transactional
    public void hardDeleteGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(GAME_NOT_FOUND + id));

        try {
            gameRepository.delete(game);
            gameRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar físicamente el juego con ID " + id + " porque tiene puntajes asociados", e);
        }
    }

    private Game findActiveGame(Long id) {
        return gameRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException(GAME_NOT_FOUND + id));
    }

    private GameDTO mapToDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getTitle(),
                game.getDescription(),
                game.getVersion(),
                game.getGenre(),
                game.getDeveloperName(),
                game.getCoverImageUrl(),
                game.getDownloadUrl(),
                game.getCreatedAt()
        );
    }
}