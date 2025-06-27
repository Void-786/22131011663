package com.project.urlshortener.entity;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShortUrl {
    private String shortcode;
    private String originalUrl;
    private Instant createdAt;
    private Instant expiry;
    private int clickCount = 0;
    private List<ClickEvent> clicks = new ArrayList<>();
}
