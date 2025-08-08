package dev.shafin.urlshortener.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrl extends AuditTrail {

    public ShortUrl(String slug, String originalUrl) {
        this.slug = slug;
        this.originalUrl = originalUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false)
    private int clickCount = 0;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    public synchronized void incrementClickCountAndSetLastAccessed(LocalDateTime time) {
        this.clickCount++;
        this.lastAccessedAt = time;
    }
}