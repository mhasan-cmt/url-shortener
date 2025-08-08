package dev.shafin.urlshortener.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public void incrementClickCount() {
        this.clickCount++;
    }
}