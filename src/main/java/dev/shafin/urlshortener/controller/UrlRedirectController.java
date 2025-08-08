package dev.shafin.urlshortener.controller;

import dev.shafin.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public void redirectToOriginal(@PathVariable String slug, HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.findBySlug(slug).ifPresentOrElse(shortUrl -> {
            try {
                service.recordClick(shortUrl, request);
                response.sendRedirect(shortUrl.getOriginalUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            try {
                response.sendError(HttpStatus.NOT_FOUND.value(), "Short URL not found");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
