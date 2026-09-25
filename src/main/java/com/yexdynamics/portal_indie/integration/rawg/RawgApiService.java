package com.yexdynamics.portal_indie.integration.rawg;

import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGameDetailDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGameDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgGameListResponseDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgStoreLinkDto;
import com.yexdynamics.portal_indie.integration.rawg.dto.RawgStoreListResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class RawgApiService {

    private final RestClient restClient;
    private final String apiKey;

    public RawgApiService(
            RestClient.Builder restClientBuilder,
            @Value("${rawg.api.base-url}") String baseUrl,
            @Value("${rawg.api.key}") String apiKey
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public List<RawgGameDto> searchIndieGames(int limit) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("⚠️ RAWG_API_KEY no está configurada o está vacía en las variables de entorno.");
        }

        RawgGameListResponseDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", apiKey)
                        .queryParam("ordering", "-rating")
                        .queryParam("page_size", limit)
                        .build())
                .retrieve()
                .body(RawgGameListResponseDto.class);

        return response != null && response.getResults() != null
                ? response.getResults()
                : Collections.emptyList();
    }

    public RawgGameDetailDto getGameDetail(Long id) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/games/{id}")
                            .queryParam("key", apiKey)
                            .build(id))
                    .retrieve()
                    .body(RawgGameDetailDto.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("Juego externo no encontrado en RAWG con ID: " + id);
        }
    }

    public List<RawgStoreLinkDto> getGameStores(Long id) {
        try {
            RawgStoreListResponseDto response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/games/{id}/stores")
                            .queryParam("key", apiKey)
                            .build(id))
                    .retrieve()
                    .body(RawgStoreListResponseDto.class);

            return response != null && response.getResults() != null
                    ? response.getResults()
                    : Collections.emptyList();

        } catch (RestClientException e) {
            log.warn("No se pudieron obtener las tiendas del juego externo ID {}: {}", id, e.getMessage());
            return Collections.emptyList();
        }
    }
}
