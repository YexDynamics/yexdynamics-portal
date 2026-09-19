package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.CreateGameDTO;
import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.dto.UpdateGameDTO;

import java.util.List;

public interface GameService {
    List<GameDTO> getAllGames();
    GameDTO getGameById(Long id);
    GameDTO createGame(CreateGameDTO createGameDTO);
    GameDTO updateGame(Long id, UpdateGameDTO updateGameDTO);
    void deleteGame(Long id);
    void hardDeleteGame(Long id);
}