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

### Direct Compile Dependencies (declared in pom.xml)

| Library | Purpose |
|---------|---------|
| `spring-boot-starter-webmvc` | Spring MVC web framework, Tomcat, Jackson |
| `spring-boot-starter-data-jpa` | JPA/Hibernate, HikariCP connection pool |
| `spring-boot-starter-validation` | Bean validation (Hibernate Validator) |
| `spring-boot-starter-actuator` | Health, info, and metrics endpoints |
| `spring-boot-starter-thymeleaf` | Server-rendered HTML templates |
| `thymeleaf-extras-springsecurity6` | `sec:` attributes in templates |
| `spring-boot-starter-security` | Authentication, authorization, CSRF |
| `spring-boot-starter-oauth2-client` | Google OAuth2 login in production |
| `spring-boot-starter-oauth2-resource-server` | JWT encode/decode for REST API |
| `micrometer-registry-prometheus` | Prometheus metrics export |
| `springdoc-openapi-starter-webmvc-ui` 2.8.8 | Swagger UI + OpenAPI spec |
| `postgresql` (runtime) | PostgreSQL JDBC driver |
| `spring-boot-starter-flyway` | Database migration framework |
| `flyway-database-postgresql` | PostgreSQL-specific Flyway dialect |
| `openpdf` 2.0.4 | PDF report generation |

### Key Indirect Dependencies (transitive, managed by Spring Boot BOM)

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| Hibernate ORM | 7.2.4 | JPA implementation, SQL generation | `starter-data-jpa` |
| HikariCP | 7.0.2 | JDBC connection pool | `starter-data-jpa` |
| Spring Data JPA | 4.0.3 | Repository abstraction, query derivation | `starter-data-jpa` |
| Spring Security | 7.0.3 | Core security (config, web, crypto, OAuth2) | `starter-security` |
| Nimbus JOSE JWT | 10.4 | JWT signing/verification (HMAC-SHA256) | `oauth2-resource-server` |
| Nimbus OAuth2 OIDC SDK | 11.26.1 | OpenID Connect client support | `oauth2-client` |
| Jackson | 3.0.4 (core) / 2.20.2 (dataformat) | JSON serialization, YAML config | `starter-webmvc`, `flyway` |
| Tomcat Embed | 11.0.18 | Servlet container (core, EL, WebSocket) | `starter-webmvc` |
| Thymeleaf | 3.1.3 | Template engine (includes attoparser, unbescape) | `starter-thymeleaf` |
| Logback | 1.5.32 | Logging implementation | `starter-logging` |
| SLF4J | 2.0.17 | Logging API (includes jul-to-slf4j, log4j-to-slf4j bridges) | `starter-logging` |
| Flyway Core | 11.14.1 | Migration engine | `starter-flyway` |
| Micrometer | 1.16.3 | Metrics collection (core, observation, commons) | `starter-actuator` |
| Prometheus Client | 1.4.3 | Metrics exposition (model, config, text formats) | `micrometer-registry-prometheus` |
| Hibernate Validator | 9.0.1 | Bean validation implementation | `starter-validation` |
| Swagger / OpenAPI | 2.2.30 | API annotations, models, core | `springdoc-openapi` |
| AspectJ Weaver | 1.9.25 | AOP proxy support for `@Transactional` | `starter-data-jpa` |
| ANTLR4 Runtime | 4.13.2 | HQL/JPQL query parsing | `spring-data-jpa` |
| SnakeYAML | 2.5 | YAML configuration parsing | `spring-boot-starter` |
| Byte Buddy | 1.17.8 | Runtime class generation (Hibernate proxies) | `hibernate-core` |
| JAXB Runtime | 4.0.6 | XML binding (Hibernate metadata) | `hibernate-core` |
| Jakarta APIs | 3.x | Persistence, Validation, Transaction, Annotation, Inject | Spring Boot BOM |

### Direct Test Dependencies

| Library | Purpose |
|---------|---------|
| `spring-boot-starter-test` | JUnit 6, Mockito, AssertJ, Hamcrest, JsonPath |
| `spring-boot-starter-webmvc-test` | MockMvc for controller integration tests |
| `spring-security-test` | `SecurityMockMvcRequestPostProcessors` (user, csrf) |
| `archunit-junit5` 1.4.1 | Architecture rule enforcement (hexagonal boundaries) |

### Frontend (no build step)

| Library | Version | Loaded via |
|---------|---------|-----------|
| htmx | 2.0.4 | CDN `<script>` in layout.html |

## Workflow After Commits

After every commit, push and rebuild/rerun docker:
```bash
git push && docker compose up -d --build app
```

## ADRs

Architecture decisions are in `doc/adr/`. Read relevant ADRs before making changes in their area.
