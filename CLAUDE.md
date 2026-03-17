# CLAUDE.md

Conventions and AI-specific workflow for this project. For full setup, build commands, and code style details, see [README.md](README.md).

## Quick Reference

- `./mvnw spring-boot:run` — Run the app (dev profile, port 8080, login: dev/dev)
- `./mvnw verify` — Full verification: checkstyle, compile, tests, coverage, javadoc
- `./mvnw test` — Run tests (requires dev DB on port 5434)

## Architecture Rules

Hexagonal architecture enforced by ArchUnit (see [ADR-0002](doc/adr/0002-use-hexagonal-architecture.md)):

- **Domain** (`domain/`) must not depend on application or infrastructure
- **Application** (`application/web/`) must not depend on infrastructure
- **Infrastructure** (`infrastructure/`) may depend on domain and application

## Key Conventions

- **Checkstyle**: 750-line files, 30-line methods, no star/unused/redundant imports, braces required. See README for full table.
- **Tests**: `@DisplayName` required, name methods `should<Expected>When<Condition>`, 85% line coverage enforced by JaCoCo.
- **Javadoc**: All production classes require Javadoc with Mermaid diagrams ([ADR-0022](doc/adr/0022-javadoc-standard-with-mermaid-diagrams.md)). Models/ports use `classDiagram`, controllers/services use `sequenceDiagram`, config classes use `flowchart`.

## External Libraries

Only add libraries when highly useful. Every addition increases attack surface, build time, and upgrade burden.

### Compile Dependencies

| Library | Purpose | Pulled in by |
|---------|---------|-------------|
| Spring Boot 4.0.3 Starters | Framework (web, JPA, security, actuator, validation, Thymeleaf, Flyway) | Direct |
| `thymeleaf-extras-springsecurity6` | `sec:` attributes in templates (authorize, authentication) | Direct |
| `spring-boot-starter-oauth2-client` | Google OAuth2 login in production | Direct |
| `spring-boot-starter-oauth2-resource-server` | JWT encode/decode (`NimbusJwtEncoder`/`Decoder`) for REST API | Direct |
| `micrometer-registry-prometheus` | Prometheus metrics export at `/actuator/prometheus` | Direct |
| `springdoc-openapi-starter-webmvc-ui` 2.8.8 | Swagger UI + OpenAPI spec generation at `/swagger-ui.html` | Direct |
| `postgresql` | PostgreSQL JDBC driver | Direct (runtime) |
| `flyway-database-postgresql` | PostgreSQL-specific Flyway migration support | Direct |
| `openpdf` 2.0.4 | PDF report generation (service summary, item history) | Direct |
| Hibernate ORM 7.2 | JPA implementation | Via `spring-boot-starter-data-jpa` |
| Nimbus JOSE JWT | JWT signing/verification (HMAC-SHA256) | Via `oauth2-resource-server` |
| Jackson 3.x | JSON serialization for REST API | Via `spring-boot-starter-webmvc` |
| HikariCP | JDBC connection pool | Via `spring-boot-starter-data-jpa` |
| Logback + SLF4J | Logging | Via `spring-boot-starter` |
| Tomcat Embed | Servlet container | Via `spring-boot-starter-webmvc` |

### Test Dependencies

| Library | Purpose |
|---------|---------|
| `spring-boot-starter-test` | JUnit 6, Mockito, AssertJ, Hamcrest, JsonPath |
| `spring-boot-starter-webmvc-test` | MockMvc for controller integration tests |
| `spring-security-test` | `SecurityMockMvcRequestPostProcessors` (user, csrf) |
| `archunit-junit5` 1.4.1 | Architecture rule enforcement (hexagonal boundaries) |

### Frontend (no build step)

| Library | Purpose | Loaded via |
|---------|---------|-----------|
| htmx 2.0.4 | Lightweight AJAX for dynamic UI | CDN (`<script>` in layout.html) |

## Workflow After Commits

After every commit, push and rebuild/rerun docker:
```bash
git push && docker compose up -d --build app
```

## ADRs

Architecture decisions are in `doc/adr/`. Read relevant ADRs before making changes in their area.
