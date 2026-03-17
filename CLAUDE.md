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

### All Indirect Compile/Runtime Dependencies

Transitive dependencies, managed by Spring Boot 4.0.3 BOM unless version noted.

#### Spring Framework 7.0.5

| Module | Purpose | Pulled in by |
|--------|---------|-------------|
| `spring-core` | Core utilities, resource loading | All starters |
| `spring-beans` | Dependency injection | `spring-core` |
| `spring-context` | ApplicationContext, events, scheduling | `spring-beans` |
| `spring-expression` | SpEL expression language | `spring-context` |
| `spring-aop` | AOP proxy support | `spring-context` |
| `spring-aspects` | AspectJ integration for `@Transactional` | `starter-data-jpa` |
| `spring-web` | HTTP abstractions, RestClient | `starter-webmvc` |
| `spring-webmvc` | DispatcherServlet, controllers | `starter-webmvc` |
| `spring-jdbc` | JdbcTemplate, DataSource | `starter-data-jpa` |
| `spring-orm` | JPA/Hibernate integration | `starter-data-jpa` |
| `spring-tx` | `@Transactional` support | `starter-data-jpa` |

#### Spring Security 7.0.3

| Module | Purpose | Pulled in by |
|--------|---------|-------------|
| `spring-security-core` | Authentication, authorization | `starter-security` |
| `spring-security-config` | Security Java config (`HttpSecurity`) | `starter-security` |
| `spring-security-web` | Filter chains, CSRF, session management | `starter-security` |
| `spring-security-crypto` | Password encoding (BCrypt) | `spring-security-core` |
| `spring-security-oauth2-core` | OAuth2 token model, authentication | `starter-oauth2-client` |
| `spring-security-oauth2-client` | OAuth2 login flow (Google) | `starter-oauth2-client` |
| `spring-security-oauth2-jose` | JWT encode/decode, JWK support | `starter-oauth2-resource-server` |
| `spring-security-oauth2-resource-server` | Bearer token filter | `starter-oauth2-resource-server` |

#### Spring Data 4.0.3

| Module | Purpose | Pulled in by |
|--------|---------|-------------|
| `spring-data-commons` | Repository abstraction, pagination | `starter-data-jpa` |
| `spring-data-jpa` | JPA repository implementation, query derivation | `starter-data-jpa` |

#### Persistence

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `hibernate-core` | 7.2.4 | JPA implementation, SQL generation | `starter-data-jpa` |
| `hibernate-models` | 1.0.1 | Hibernate metadata model | `hibernate-core` |
| `hibernate-validator` | 9.0.1 | Bean validation implementation | `starter-validation` |
| HikariCP | 7.0.2 | JDBC connection pool | `starter-data-jpa` |
| `flyway-core` | 11.14.1 | Database migration engine | `starter-flyway` |
| `postgresql` | 42.7.10 | PostgreSQL JDBC driver | Direct (runtime) |

#### Web & Servlet

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `tomcat-embed-core` | 11.0.18 | Embedded servlet container | `starter-webmvc` |
| `tomcat-embed-el` | 11.0.18 | Expression Language for validation | `starter-validation` |
| `tomcat-embed-websocket` | 11.0.18 | WebSocket support | `starter-webmvc` |

#### Templating

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `thymeleaf` | 3.1.3 | Template engine | `starter-thymeleaf` |
| `thymeleaf-spring6` | 3.1.3 | Spring MVC integration | `starter-thymeleaf` |
| `attoparser` | 2.0.7 | HTML parser for Thymeleaf | `thymeleaf` |
| `unbescape` | 1.1.6 | HTML/JS/CSS escaping | `thymeleaf` |

#### JSON & Serialization

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `jackson-databind` (tools.jackson) | 3.0.4 | JSON serialization for REST API | `starter-webmvc` |
| `jackson-core` (tools.jackson) | 3.0.4 | Streaming JSON parser | `jackson-databind` |
| `jackson-databind` (com.fasterxml) | 2.20.2 | JSON for Flyway, SpringDoc | `flyway-core`, `springdoc` |
| `jackson-core` (com.fasterxml) | 2.20.2 | Streaming JSON parser | `jackson-databind` |
| `jackson-annotations` | 2.20 | JSON annotations | `jackson-databind` |
| `jackson-dataformat-yaml` | 2.20.2 | YAML parsing for OpenAPI | `swagger-core` |
| `jackson-datatype-jsr310` | 2.20.2 | Java 8 date/time serialization | `swagger-core` |

#### JWT & OAuth2

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `nimbus-jose-jwt` | 10.4 | JWT signing/verification (HMAC-SHA256) | `oauth2-jose` |
| `oauth2-oidc-sdk` | 11.26.1 | OpenID Connect protocol support | `oauth2-client` |
| `content-type` | 2.3 | HTTP content-type parsing | `nimbus-jose-jwt` |
| `lang-tag` | 1.7 | Language tag parsing | `oauth2-oidc-sdk` |
| `jcip-annotations` | 1.0-1 | Thread-safety annotations | `nimbus-jose-jwt` |
| `json-smart` | 2.6.0 | Fast JSON parser (Nimbus) | `nimbus-jose-jwt` |
| `accessors-smart` | 2.6.0 | Reflection for json-smart | `json-smart` |

#### Logging

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `slf4j-api` | 2.0.17 | Logging API | `starter-logging` |
| `logback-classic` | 1.5.32 | SLF4J logging implementation | `starter-logging` |
| `logback-core` | 1.5.32 | Logback internals | `logback-classic` |
| `jul-to-slf4j` | 2.0.17 | java.util.logging bridge | `starter-logging` |
| `log4j-to-slf4j` | 2.25.3 | Log4j2 → SLF4J bridge | `starter-logging` |
| `log4j-api` | 2.25.3 | Log4j2 API (bridged to SLF4J) | `log4j-to-slf4j` |
| `jboss-logging` | 3.6.2 | Logging used by Hibernate | `hibernate-core` |

#### Metrics & Monitoring

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `micrometer-core` | 1.16.3 | Metrics collection engine | `starter-actuator` |
| `micrometer-commons` | 1.16.3 | Shared metric utilities | `micrometer-core` |
| `micrometer-observation` | 1.16.3 | Observation API | `starter-actuator` |
| `micrometer-jakarta9` | 1.16.3 | Jakarta servlet metrics | `starter-actuator` |
| `prometheus-metrics-core` | 1.4.3 | Prometheus metric types | `micrometer-registry-prometheus` |
| `prometheus-metrics-model` | 1.4.3 | Prometheus data model | `prometheus-metrics-core` |
| `prometheus-metrics-config` | 1.4.3 | Prometheus configuration | `prometheus-metrics-core` |
| `prometheus-metrics-tracer-common` | 1.4.3 | Tracing integration | `micrometer-registry-prometheus` |
| `prometheus-metrics-exposition-formats` | 1.4.3 | Text exposition format | `micrometer-registry-prometheus` |
| `prometheus-metrics-exposition-textformats` | 1.4.3 | Text format writer | `prometheus-exposition-formats` |
| HdrHistogram | 2.2.2 | High-performance histogram | `micrometer-core` |
| LatencyUtils | 2.0.3 | Latency recording | `micrometer-core` |

#### API Documentation

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `springdoc-openapi-starter-webmvc-api` | 2.8.8 | OpenAPI generation | `springdoc-openapi-starter-webmvc-ui` |
| `springdoc-openapi-starter-common` | 2.8.8 | Shared OpenAPI utilities | `springdoc-webmvc-api` |
| `swagger-core-jakarta` | 2.2.30 | OpenAPI model processing | `springdoc-common` |
| `swagger-annotations-jakarta` | 2.2.30 | `@Operation`, `@Parameter`, etc. | `swagger-core` |
| `swagger-models-jakarta` | 2.2.30 | OpenAPI schema model | `swagger-core` |
| `swagger-ui` (WebJar) | 5.21.0 | Swagger UI static assets | `springdoc-openapi-starter-webmvc-ui` |
| `webjars-locator-lite` | 1.1.3 | WebJar path resolution | `springdoc-openapi-starter-webmvc-ui` |

#### Jakarta APIs

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `jakarta.persistence-api` | 3.2.0 | JPA annotations (`@Entity`, `@Column`) | `hibernate-core` |
| `jakarta.validation-api` | 3.1.1 | Validation annotations (`@NotNull`) | `hibernate-validator` |
| `jakarta.transaction-api` | 2.0.1 | `@Transactional` | `hibernate-core` |
| `jakarta.annotation-api` | 3.0.0 | `@PostConstruct`, `@PreDestroy` | `spring-boot-starter` |
| `jakarta.inject-api` | 2.0.1 | `@Inject` (CDI) | `hibernate-core` |
| `jakarta.xml.bind-api` | 4.0.4 | JAXB annotations | `spring-boot-starter-test` |
| `jakarta.activation-api` | 2.1.4 | MIME type handling | `jakarta.xml.bind-api` |

#### XML & JAXB (runtime, used by Hibernate)

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `jaxb-runtime` | 4.0.6 | JAXB implementation | `hibernate-core` |
| `jaxb-core` | 4.0.6 | JAXB shared core | `jaxb-runtime` |
| `txw2` | 4.0.6 | XML writing | `jaxb-core` |
| `angus-activation` | 2.0.3 | Jakarta Activation impl | `jaxb-core` |
| `istack-commons-runtime` | 4.1.2 | JAXB utilities | `jaxb-core` |

#### Bytecode & Reflection

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `byte-buddy` | 1.17.8 | Runtime proxy generation | `hibernate-core` |
| `aspectjweaver` | 1.9.25.1 | AOP weaving for `@Transactional` | `spring-aspects` |
| ASM | 9.7.1 | Bytecode manipulation | `accessors-smart` |

#### Utility

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| SnakeYAML | 2.5 | YAML config parsing | `spring-boot-starter` |
| `commons-logging` | 1.3.5 | Logging bridge (Spring Core) | `spring-core` |
| `commons-lang3` | 3.19.0 | String/object utilities | `swagger-core` |
| `classmate` | 1.7.3 | Type resolution (validation) | `hibernate-validator` |
| `checker-qual` | 3.52.0 | Nullness annotations | `postgresql` |
| `jspecify` | 1.0.0 | Nullness annotations | `micrometer` |
| ANTLR4 Runtime | 4.13.2 | HQL/JPQL query parsing | `spring-data-jpa` |

### Direct Test Dependencies

| Library | Purpose |
|---------|---------|
| `spring-boot-starter-test` | JUnit 6, Mockito, AssertJ, Hamcrest, JsonPath |
| `spring-boot-starter-webmvc-test` | MockMvc for controller integration tests |
| `spring-security-test` | `SecurityMockMvcRequestPostProcessors` (user, csrf) |
| `archunit-junit5` 1.4.1 | Architecture rule enforcement (hexagonal boundaries) |

### Indirect Test Dependencies

| Library | Version | Purpose | Pulled in by |
|---------|---------|---------|-------------|
| `junit-jupiter` | 6.0.3 | JUnit 5 test engine + API + params | `starter-test` |
| `junit-platform-commons` | 6.0.3 | JUnit platform utilities | `junit-jupiter` |
| `junit-platform-engine` | 6.0.3 | Test engine SPI | `junit-jupiter-engine` |
| `opentest4j` | 1.3.0 | Test assertion exceptions | `junit-jupiter-api` |
| `apiguardian-api` | 1.1.2 | API stability annotations | `junit-jupiter-api` |
| `mockito-core` | 5.20.0 | Mocking framework | `starter-test` |
| `mockito-junit-jupiter` | 5.20.0 | Mockito JUnit 5 extension | `starter-test` |
| `byte-buddy-agent` | 1.17.8 | Mockito class instrumentation | `mockito-core` |
| `objenesis` | 3.3 | Object instantiation without constructors | `mockito-core` |
| `assertj-core` | 3.27.7 | Fluent assertions | `starter-test` |
| `hamcrest` | 3.0 | Matcher-based assertions | `starter-test` |
| `json-path` | 2.10.0 | JSON path expressions for MockMvc | `starter-test` |
| `jsonassert` | 1.5.3 | JSON comparison assertions | `starter-test` |
| `android-json` | 0.0.20131108 | JSON implementation for jsonassert | `jsonassert` |
| `awaitility` | 4.3.0 | Async testing utilities | `starter-test` |
| `xmlunit-core` | 2.10.4 | XML comparison | `starter-test` |

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
