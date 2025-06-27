package com.project.urlshortener.controller;

import com.project.urlshortener.dto.ShortUrlRequest;
import com.project.urlshortener.dto.ShortUrlResponse;
import com.project.urlshortener.dto.StatsResponse;
import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/shorturls")
    public ResponseEntity<?> createShortUrl(@Valid @RequestBody ShortUrlRequest req, HttpServletRequest servletReq) {
        if (!service.isValidUrl(req.getUrl())) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Invalid URL format")
            );
        }
        String code = req.getShortcode();
        if (StringUtils.hasText(code) && service.exists(code)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ErrorResponse("Shortcode already in use")
            );
        }
        ShortUrl su = service.createShortUrl(req.getUrl(), req.getValidity(), code);
        String host = servletReq.getRequestURL().toString().replace(servletReq.getRequestURI(), "");
        String shortLink = host + "/" + su.getShortcode();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ShortUrlResponse(shortLink, su.getExpiry().toString()));
    }

    @GetMapping("/{shortcode}")
    public ResponseEntity<?> redirect(@PathVariable String shortcode, HttpServletRequest req) {
        ShortUrl su = service.getShortUrl(shortcode);
        if (su == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Shortcode not found"));
        }
        if (Instant.now().isAfter(su.getExpiry())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(new ErrorResponse("Short link expired"));
        }
        String referrer = req.getHeader("referer");
        String geo = "unknown"; // For demo; use IP geo lookup in production
        service.recordClick(shortcode, referrer, geo);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", su.getOriginalUrl())
                .build();
    }

    @GetMapping("/shorturls/{shortcode}")
    public ResponseEntity<?> getStats(@PathVariable String shortcode) {
        ShortUrl su = service.getShortUrl(shortcode);
        if (su == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Shortcode not found"));
        }
        return ResponseEntity.ok(new StatsResponse(
                su.getOriginalUrl(),
                su.getCreatedAt().toString(),
                su.getExpiry().toString(),
                su.getClickCount(),
                su.getClicks()
        ));
    }

    static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}

