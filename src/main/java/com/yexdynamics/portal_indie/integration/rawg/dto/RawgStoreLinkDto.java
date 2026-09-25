package com.yexdynamics.portal_indie.integration.rawg.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgStoreLinkDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("url")
    private String url;
}
