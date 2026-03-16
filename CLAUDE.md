# CLAUDE.md

## Project Overview

Maintainly (MyStuff) is a property maintenance tracking application built with Spring Boot 4.0.3, Thymeleaf + HTMX, and PostgreSQL 17. It uses hexagonal architecture enforced by ArchUnit.

## Build Commands

- `./mvnw spring-boot:run` — Run the app (dev profile, port 8080, login: dev/dev)
- `./mvnw compile` — Compile only
- `./mvnw test` — Run tests (requires dev DB on port 5434)
- `./mvnw verify` — Full verification: checkstyle, compile, tests, coverage, javadoc
- `./mvnw test jacoco:report` — Generate coverage report at `target/site/jacoco/index.html`
- `./mvnw javadoc:javadoc` — Generate Javadoc at `target/reports/apidocs/index.html`

## Dev Database

```bash
docker run -d --name mystuff-dev-db -e POSTGRES_DB=mystuffdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=test -p 5434:5432 postgres:17
```

## Architecture Rules

This project uses **hexagonal architecture** (ports and adapters). These boundaries are enforced by ArchUnit tests and must not be violated:

- **Domain** (`domain/`) must not depend on application or infrastructure
- **Application** (`application/web/`) may depend on domain only
- **Infrastructure** (`infrastructure/`) may depend on domain (and application for config wiring)

Package structure:
```
solutions.mystuff.application.web    — Controllers, PDF generators, input validation
solutions.mystuff.domain.model       — JPA entities
solutions.mystuff.domain.port.in     — Inbound use-case interfaces
solutions.mystuff.domain.port.out    — Outbound repository interfaces
solutions.mystuff.domain.service     — Domain service implementations
solutions.mystuff.infrastructure     — Config, persistence adapters, filters
```

## Code Quality Gates

All enforced by `./mvnw verify`:

| Rule | Enforcement |
|------|-------------|
| File length max 750 lines | Checkstyle |
| Method length max 30 lines | Checkstyle |
| Braces required on all blocks | Checkstyle |
| Star imports forbidden | Checkstyle |
| Unused/redundant imports forbidden | Checkstyle |
| 85% minimum line coverage | JaCoCo |
| Hexagonal boundaries | ArchUnit |
| Javadoc on all production classes | Javadoc plugin |

## Test Conventions

- All test methods require `@DisplayName`
- Method naming: `should<Expected>When<Condition>`
- **Unit tests**: JUnit 5 + Mockito, no Spring context
- **Integration tests**: `@SpringBootTest` + `@AutoConfigureMockMvc` against PostgreSQL
- **Architecture tests**: ArchUnit verifying hexagonal boundaries

## Javadoc Standard

All production classes require Javadoc with an embedded Mermaid diagram (ADR-0022):

- **Models and ports** use `classDiagram`
- **Controllers and services** use `sequenceDiagram`
- **Configuration classes** use `flowchart`

Diagrams use `<div class="mermaid">` blocks in Javadoc comments.

## Workflow After Commits

After every commit, push and rebuild/rerun docker:
```bash
git push && docker compose up -d --build app
```

## Key ADRs

Architecture decisions are in `doc/adr/`. Read relevant ADRs before making changes in their area.
