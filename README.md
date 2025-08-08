# URL Shortener (Spring Boot)

A minimal Bit.ly-style URL shortener built with Spring Boot.
Features:
- Shorten long URLs (auto-generated slug)
- Custom slugs
- Redirect on access
- Per-click analytics (stored per click)
- Simple in-memory rate limiting for unauthenticated users (Bucket4j)
- Profiles: `local` uses H2, `prod` uses PostgreSQL (see `application-*.yaml`)

---

## Quick start (local)

1. Build & run (local uses `application-local.yaml` which uses H2 in-memory):
```bash
./mvnw clean package
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
2. Shorten a URL:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"url":"https://example.com"}' http://localhost:8080/api/shorten
```
3. Use the short url:
   Open http://localhost:8080/{slug} in a browser — it redirects to the original URL and stores a click event.
4. Check analytics:
```bash
curl http://localhost:8080/api/analytics/{slug}
```
## Endpoints
| Method | Path                        | Body                          | Response | Description |
|--------|-----------------------------|-------------------------------|----------|-------------|
| POST   | /api/shorten                | `{ "url": "...", "customSlug": "optional" }` | `{ "shortUrl": "http://localhost:8080/slug" }` | Shorten a URL |
| GET    | /{slug}                     | -                             | 302 redirect to original URL | Redirect to original URL |
| GET    | /api/analytics/{slug}       | -                             | `{ "slug": "...", "createdAt": "LocalDateTime", "totalClicks": 1, "originalUrl": "...", "clicksLast7Days": 1, "lastAccessedAt": "LocalDateTime", "clicksLast24h": 1 }` | Get analytics for a slug |


## Rate Limiting
- Implemented with Bucket4j in-memory buckets keyed by client IP.

- Default configuration: 10 shorten requests per IP per hour.

- This is an in-memory solution (not distributed). For distributed environments, we might have to switch to a Redis-based bucket store or a distributed token-bucket implementation.

## Database
- ```application-local.yaml``` (H2) —  for development

- ```application-prod.yaml``` (PostgreSQL) — configure credentials/host

- Tables (auto-created by JPA if ddl-auto=update):

- ```short_urls``` — stores slug, original URL, createdAt, clickCount, lastAccessedAt

- ```click_events``` — each click with timestamp, IP, user agent, referrer
