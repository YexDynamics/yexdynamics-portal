package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.CreateGameDTO;
import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.service.GameService;
import com.yexdynamics.portal_indie.persistence.entity.Game;
import com.yexdynamics.portal_indie.persistence.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GameDTO getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado con ID: " + id));
        return mapToDTO(game);
    }

    @Override
    @Transactional
    public GameDTO createGame(CreateGameDTO dto) {
        if (gameRepository.findByTitle(dto.getTitle()).isPresent()) {
            throw new RuntimeException("Ya existe un juego registrado con el título: " + dto.getTitle());
        }

        Game game = new Game();
        game.setTitle(dto.getTitle());
        game.setDescription(dto.getDescription());
        game.setVersion(dto.getVersion() != null ? dto.getVersion() : "1.0.0");
        game.setGenre(dto.getGenre());
        game.setDeveloperName(dto.getDeveloperName());
        game.setCoverImageUrl(dto.getCoverImageUrl());
        game.setDownloadUrl(dto.getDownloadUrl());

        Game savedGame = gameRepository.save(game);
        return mapToDTO(savedGame);
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