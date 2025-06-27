package com.project.urlshortener.service;

import com.project.urlshortener.entity.ClickEvent;
import com.project.urlshortener.entity.ShortUrl;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {
    private final Map<String, ShortUrl> storage = new ConcurrentHashMap<>();
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORTCODE_LENGTH = 6;
    private static final int DEFAULT_VALIDITY = 30;

    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public boolean exists(String shortcode) {
        return storage.containsKey(shortcode);
    }

    public String generateShortcode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            Random rnd = new Random();
            for (int i = 0; i < SHORTCODE_LENGTH; i++) {
                sb.append(ALPHANUM.charAt(rnd.nextInt(ALPHANUM.length())));
            }
            code = sb.toString();
        } while (storage.containsKey(code));
        return code;
    }

    public ShortUrl createShortUrl(String url, Integer validity, String customShortcode) {
        String code = customShortcode != null ? customShortcode : generateShortcode();
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(60L * (validity != null ? validity : DEFAULT_VALIDITY));
        ShortUrl su = new ShortUrl();
        su.setShortcode(code);
        su.setOriginalUrl(url);
        su.setCreatedAt(now);
        su.setExpiry(expiry);
        storage.put(code, su);
        return su;
    }

    public ShortUrl getShortUrl(String shortcode) {
        return storage.get(shortcode);
    }

    public void recordClick(String shortcode, String referrer, String geo) {
        ShortUrl su = storage.get(shortcode);
        if (su != null) {
            su.setClickCount(su.getClickCount() + 1);
            su.getClicks().add(new ClickEvent(Instant.now(), referrer, geo));
        }
    }
}

