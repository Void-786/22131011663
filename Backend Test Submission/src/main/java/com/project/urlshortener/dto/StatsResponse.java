package com.project.urlshortener.dto;

import com.project.urlshortener.entity.ClickEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatsResponse {
    private String originalUrl;
    private String createdAt;
    private String expiry;
    private int totalClicks;
    private List<ClickEvent> clicks;
}

