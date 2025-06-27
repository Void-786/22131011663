package com.project.loggingmiddleware;


import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Component
public class RemoteLogger {
    private static final String LOG_API = "http://20.244.56.144/evaluation-service/logs";
    private static final Set<String> STACKS = Set.of("backend", "frontend");
    private static final Set<String> LEVELS = Set.of("debug", "info", "warn", "error", "fatal");
    private static final Set<String> BACKEND_PACKAGES = Set.of(
            "cache", "controller", "cron job", "db", "domain", "handler", "repository", "route", "service"
    );
    private static final Set<String> FRONTEND_PACKAGES = Set.of(
            "api", "component", "hook", "page", "state", "style"
    );
    private static final Set<String> SHARED_PACKAGES = Set.of(
            "auth", "config", "middleware", "utils"
    );

    private final RestTemplate restTemplate = new RestTemplate();

    public void log(String stack, String level, String pkg, String message) {
        if (!STACKS.contains(stack)) throw new IllegalArgumentException("Invalid stack: " + stack);
        if (!LEVELS.contains(level)) throw new IllegalArgumentException("Invalid level: " + level);
        if (!BACKEND_PACKAGES.contains(pkg) && !FRONTEND_PACKAGES.contains(pkg) && !SHARED_PACKAGES.contains(pkg))
            throw new IllegalArgumentException("Invalid package: " + pkg);

        Map<String, String> body = Map.of(
                "stack", stack,
                "level", level,
                "package", pkg,
                "message", message
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(LOG_API, entity, String.class);
        } catch (Exception ex) {
            // Optionally handle/log the failure
        }
    }
}


