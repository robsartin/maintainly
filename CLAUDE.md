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

## Workflow After Commits

After every commit, push and rebuild/rerun docker:
```bash
git push && docker compose up -d --build app
```

## ADRs

Architecture decisions are in `doc/adr/`. Read relevant ADRs before making changes in their area.
