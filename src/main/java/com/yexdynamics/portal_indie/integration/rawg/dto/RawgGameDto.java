package com.yexdynamics.portal_indie.integration.rawg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGameDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("released")
    private String released;

    @JsonProperty("background_image")
    private String backgroundImage;

    @JsonProperty("rating")
    private Double rating;

    @JsonProperty("genres")
    private List<RawgGenreDto> genres;

    @JsonProperty("platforms")
    private List<RawgPlatformEntryDto> platforms;
}
