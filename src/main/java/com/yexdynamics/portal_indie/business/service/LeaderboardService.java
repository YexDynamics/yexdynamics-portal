package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.LeaderboardDTO;
import com.yexdynamics.portal_indie.business.dto.LeaderboardResponseDTO;

import java.util.List;

public interface LeaderboardService {

    LeaderboardResponseDTO createScore(LeaderboardDTO leaderboardDTO);

    List<LeaderboardResponseDTO> getTopScoresByGame(Long gameId);

    LeaderboardResponseDTO getScoreById(Long id);

    void deleteScore(Long id);
}