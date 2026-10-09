package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.CreateGameDTO;
import com.yexdynamics.portal_indie.business.dto.GameDTO;
import com.yexdynamics.portal_indie.business.dto.UpdateGameDTO;
import com.yexdynamics.portal_indie.persistence.entity.User;

import java.util.List;

public interface GameService {
    List<GameDTO> getAllGames();
    GameDTO getGameById(Long id);
    GameDTO createGame(CreateGameDTO createGameDTO, User owner);
    void hardDeleteGame(Long id);
    void updateGameStatus(Long id, boolean active);

    List<GameDTO> getMyGames(Long ownerId);
    GameDTO getMyGameById(Long id, Long ownerId);
    GameDTO updateMyGame(Long id, UpdateGameDTO updateGameDTO, Long ownerId);
    void disableMyGame(Long id, Long ownerId);
}
