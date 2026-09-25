package com.yexdynamics.portal_indie.business.service;

import com.yexdynamics.portal_indie.business.dto.ExternalGameDetailDto;
import com.yexdynamics.portal_indie.business.dto.ExternalGameResponseDto;

import java.util.List;

public interface ExternalGameService {
    List<ExternalGameResponseDto> getPopularIndieGames(int limit);
    ExternalGameDetailDto getExternalGameById(Long id);
}
