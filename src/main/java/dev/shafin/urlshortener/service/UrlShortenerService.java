package dev.shafin.urlshortener.service;

import dev.shafin.urlshortener.model.ShortUrl;
import dev.shafin.urlshortener.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final int SLUG_LENGTH = 6;
    private static final String BASE_URL = "http://localhost:8080/";

    private final ShortUrlRepository repository;
    private final SecureRandom random = new SecureRandom();

    public String shortenUrl(String originalUrl, String customSlug) {
        String slug;

        if (customSlug != null && !customSlug.isBlank()) {
            if (repository.existsBySlug(customSlug)) {
                throw new RuntimeException("Custom slug already exists");
            }
            slug = customSlug;
        } else {
            slug = generateUniqueSlug();
        }

        ShortUrl shortUrl = new ShortUrl(slug, originalUrl);
        repository.save(shortUrl);

        return BASE_URL + slug;
    }

    public Optional<String> getOriginalUrl(String slug) {
        Optional<ShortUrl> shortUrlOpt = repository.findBySlug(slug);
        shortUrlOpt.ifPresent(url -> {
            url.incrementClickCount();
            repository.save(url);
        });

        return shortUrlOpt.map(ShortUrl::getOriginalUrl);
    }

    private String generateUniqueSlug() {
        String slug;
        do {
            slug = generateRandomSlug();
        } while (repository.existsBySlug(slug));
        return slug;
    }

    private String generateRandomSlug() {
        byte[] bytes = new byte[4]; // 32 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, SLUG_LENGTH);
    }
}
