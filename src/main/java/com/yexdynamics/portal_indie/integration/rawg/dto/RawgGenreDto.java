package com.yexdynamics.portal_indie.integration.rawg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGenreDto {

    @JsonProperty("name")
    private String name;
}
