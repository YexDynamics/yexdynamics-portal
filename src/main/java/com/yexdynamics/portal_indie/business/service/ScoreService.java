package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.ScoreRequestDTO;
import com.yexdynamics.portal_indie.business.dto.ScoreResponseDTO;
import com.yexdynamics.portal_indie.persistence.entity.User;

public interface ScoreService {
    ScoreResponseDTO submitScore(ScoreRequestDTO dto, User user);
}
