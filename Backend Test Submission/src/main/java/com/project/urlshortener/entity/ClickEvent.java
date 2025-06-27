package com.project.urlshortener.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ClickEvent {
    private Instant timestamp;
    private String referrer;
    private String geo;
}
