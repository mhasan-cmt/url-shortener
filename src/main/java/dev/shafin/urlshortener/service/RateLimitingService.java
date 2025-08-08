package dev.shafin.urlshortener.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingService {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    private static final int TOKENS = 10;
    private static final Duration DURATION = Duration.ofHours(1);

    private Bucket newBucket() {
        Refill refill = Refill.intervally(TOKENS, DURATION);
        Bandwidth limit = Bandwidth.classic(TOKENS, refill);
        return Bucket4j.builder().addLimit(limit).build();
    }

    public boolean tryConsume(String key) {
        Bucket bucket = cache.computeIfAbsent(key, k -> newBucket());
        return bucket.tryConsume(1);
    }
}