package dev.shafin.urlshortener.repository;

import dev.shafin.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findBySlug(String slug);

    boolean existsBySlug(String slug);
}