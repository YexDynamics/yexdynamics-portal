package com.yexdynamics.portal_indie.business.service.impl;

import com.yexdynamics.portal_indie.business.dto.ExternalGameDetailDto;
import com.yexdynamics.portal_indie.business.dto.ExternalGameResponseDto;
import com.yexdynamics.portal_indie.business.service.ExternalGameService;
import com.yexdynamics.portal_indie.integration.rawg.RawgApiService;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGameDetailDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGameDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGenreDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgPlatformDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgPlatformEntryDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgStoreLinkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExternalGameServiceImpl implements ExternalGameService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 40;

    private final RawgApiService rawgApiService;

    @Override
    public List<ExternalGameResponseDto> getPopularIndieGames(int limit) {
        int safeLimit = limit > 0 ? Math.min(limit, MAX_LIMIT) : DEFAULT_LIMIT;

        return rawgApiService.searchIndieGames(safeLimit)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public ExternalGameDetailDto getExternalGameById(Long id) {
        RawgGameDetailDto detail = rawgApiService.getGameDetail(id);

        List<String> storeUrls = rawgApiService.getGameStores(id)
                .stream()
                .map(RawgStoreLinkDto::getUrl)
                .filter(Objects::nonNull)
                .toList();

        return mapToDetailDto(detail, storeUrls);
    }

    private ExternalGameResponseDto mapToResponseDto(RawgGameDto game) {
        return new ExternalGameResponseDto(
                game.getId(),
                game.getName(),
                game.getReleased(),
                game.getBackgroundImage(),
                game.getRating(),
                mapGenres(game.getGenres()),
                mapPlatforms(game.getPlatforms())
        );
    }

    private ExternalGameDetailDto mapToDetailDto(RawgGameDetailDto game, List<String> storeUrls) {
        return new ExternalGameDetailDto(
                game.getId(),
                game.getName(),
                game.getReleased(),
                game.getBackgroundImage(),
                game.getRating(),
                mapGenres(game.getGenres()),
                mapPlatforms(game.getPlatforms()),
                game.getDescriptionRaw(),
                game.getWebsite(),
                game.getEsrbRating() != null ? game.getEsrbRating().getName() : null,
                storeUrls
        );
    }

    private List<String> mapGenres(List<RawgGenreDto> genres) {
        return genres == null
                ? List.of()
                : genres.stream().map(RawgGenreDto::getName).toList();
    }

    private List<String> mapPlatforms(List<RawgPlatformEntryDto> platforms) {
        return platforms == null
                ? List.of()
                : platforms.stream()
                        .map(RawgPlatformEntryDto::getPlatform)
                        .filter(Objects::nonNull)
                        .map(RawgPlatformDto::getName)
                        .toList();
    }
}
