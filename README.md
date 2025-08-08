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
