package dev.shafin.urlshortener.repository;

import dev.shafin.urlshortener.model.ClickEvent;
import dev.shafin.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByShortUrl(ShortUrl shortUrl);

    long countByShortUrlAndClickedAtAfter(ShortUrl shortUrl, LocalDateTime after);
}
