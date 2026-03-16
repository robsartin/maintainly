# Contributing to Maintainly

## Getting Started

See the [README](README.md) for prerequisites, environment setup, and build commands.

## Making Changes

### Run the full build before submitting

```bash
./mvnw verify
```

This runs checkstyle, compiles, runs all tests, checks code coverage (85% minimum), and generates Javadoc. All checks must pass.

### Code style

Checkstyle runs automatically at the `validate` phase. See the [README](README.md#manual-editing) for the full rules table.

### Architecture

The project uses hexagonal architecture enforced by ArchUnit tests:

- **Domain** (`domain/`) must not depend on application or infrastructure
- **Application** (`application/web/`) must not depend on infrastructure
- **Infrastructure** (`infrastructure/`) may depend on domain and application

See [ADR-0002](doc/adr/0002-use-hexagonal-architecture.md) for details.

### Tests

- All test methods require `@DisplayName`
- Name test methods: `should<Expected>When<Condition>`
- **Unit tests**: JUnit 5 + Mockito, no Spring context
- **Integration tests**: `@SpringBootTest` + `@AutoConfigureMockMvc`
- Minimum **85% line coverage** enforced by JaCoCo

### Javadoc

All production classes require Javadoc with an embedded Mermaid diagram. See [ADR-0022](doc/adr/0022-javadoc-standard-with-mermaid-diagrams.md) for the full standard.

### Database changes

Database migrations use Flyway. Add new migrations to `src/main/resources/db/migration/` following the `V{next}__description.sql` naming convention. Flyway runs automatically on startup.

### Architecture Decision Records

Significant design decisions are documented as ADRs in `doc/adr/`. If your change introduces a new architectural pattern or modifies an existing one, add or update an ADR using the format in [ADR-0001](doc/adr/0001-record-architecture-decisions.md).

## Submitting Changes

1. Create a feature branch from `main`
2. Make your changes and ensure `./mvnw verify` passes
3. Commit with a clear message describing the change
4. Open a pull request against `main`
