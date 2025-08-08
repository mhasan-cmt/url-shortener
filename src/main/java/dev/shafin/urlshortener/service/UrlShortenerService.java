package dev.shafin.urlshortener.service;

import dev.shafin.urlshortener.model.ClickEvent;
import dev.shafin.urlshortener.model.ShortUrl;
import dev.shafin.urlshortener.repository.ClickEventRepository;
import dev.shafin.urlshortener.repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private static final int SLUG_LENGTH = 6;
    private static final String BASE_URL = "http://localhost:8080/";

    private final ShortUrlRepository repository;
    private final ClickEventRepository clickEventRepository;
    private final SecureRandom random = new SecureRandom();
    @Value("${app.base-url:http://localhost:8080/}")
    private String baseUrl;

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

    public void recordClick(ShortUrl shortUrl, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String ua = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");

        LocalDateTime now = LocalDateTime.now();
        ClickEvent evt = new ClickEvent(shortUrl, now, ip, ua, referrer);
        clickEventRepository.save(evt);

        shortUrl.incrementClickCountAndSetLastAccessed(now);
        repository.save(shortUrl);
    }

    public Map<String, Object> getAnalytics(String slug) {
        ShortUrl shortUrl = repository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Slug not found"));

        long total = clickEventRepository.countByShortUrl(shortUrl);
        long last24 = clickEventRepository.countByShortUrlAndClickedAtAfter(shortUrl, LocalDateTime.now().minusDays(1));
        long last7 = clickEventRepository.countByShortUrlAndClickedAtAfter(shortUrl, LocalDateTime.now().minusDays(7));

        return Map.of(
                "slug", shortUrl.getSlug(),
                "originalUrl", shortUrl.getOriginalUrl(),
                "createdAt", shortUrl.getCreatedAt(),
                "lastAccessedAt", shortUrl.getLastAccessedAt(),
                "totalClicks", total,
                "clicksLast24h", last24,
                "clicksLast7Days", last7
        );
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


    private String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public Optional<ShortUrl> findBySlug(String slug) {
        return repository.findBySlug(slug);
    }
}
