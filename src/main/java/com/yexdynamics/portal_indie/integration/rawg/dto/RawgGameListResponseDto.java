package com.yexdynamics.portal_indie.integration.rawg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGameListResponseDto {

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("results")
    private List<RawgGameDto> results;
}
