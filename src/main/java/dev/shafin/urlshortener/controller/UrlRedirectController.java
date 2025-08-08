package dev.shafin.urlshortener.controller;

import dev.shafin.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UrlRedirectController {
    private final UrlShortenerService service;
    @GetMapping("{slug}")
    public void redirectToOriginal(@PathVariable String slug, HttpServletResponse response) throws IOException {
        service.getOriginalUrl(slug).ifPresentOrElse(
                originalUrl -> {
                    try {
                        response.sendRedirect(originalUrl);
                    } catch (IOException e) {
                        throw new RuntimeException("Redirection failed");
                    }
                },
                () -> {
                    try {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found");
                    } catch (IOException e) {
                        throw new RuntimeException("Error sending 404");
                    }
                }
        );
    }
}
