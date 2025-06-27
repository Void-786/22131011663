package com.project.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ShortUrlRequest {
    @NotBlank
    private String url;

    @PositiveOrZero
    private Integer validity; // in minutes

    @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "Shortcode must be 4-16 alphanumeric chars")
    private String shortcode;
}

