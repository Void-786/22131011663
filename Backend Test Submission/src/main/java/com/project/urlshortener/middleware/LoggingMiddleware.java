package com.project.urlshortener.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

@Component
public class LoggingMiddleware implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String logEntry = Instant.now() + " | " + req.getMethod() + " " + req.getRequestURI() + "\n";
        try (FileWriter fw = new FileWriter("access.log", true)) {
            fw.write(logEntry);
        }
        chain.doFilter(request, response);
        HttpServletResponse resp = (HttpServletResponse) response;
        try (FileWriter fw = new FileWriter("access.log", true)) {
            fw.write(Instant.now() + " | RESPONSE " + resp.getStatus() + "\n");
        }
    }
}

