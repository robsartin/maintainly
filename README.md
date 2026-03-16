# Maintainly (MyStuff)

Property maintenance tracking application. Manage items, service schedules, service history, and vendors across organizations.

Built with Spring Boot 4.0.3, Thymeleaf + HTMX, and PostgreSQL 17. Uses hexagonal architecture enforced by ArchUnit.

## Prerequisites

- **Java 25** — [Eclipse Temurin](https://adoptium.net/) recommended
- **Docker** — required for PostgreSQL in all environments
- **Git**

No separate Maven install is needed — the project includes a Maven wrapper (`./mvnw`).

## Environment Setup

### 1. Start the development database

Run a PostgreSQL 17 container on port 5434 for local development and testing:

```bash
docker run -d \
  --name mystuff-dev-db \
  -e POSTGRES_DB=mystuffdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=test \
  -p 5434:5432 \
  postgres:17
```

Flyway runs automatically on startup and applies any pending migrations from `src/main/resources/db/migration/`.

### 2. Run the application

```bash
./mvnw spring-boot:run
```

The app starts on **http://localhost:8080** using the default (dev) profile. Dev mode uses form login with credentials `dev`/`dev`.

Available dev endpoints:
- **Swagger UI** — http://localhost:8080/swagger-ui.html
- **OpenAPI spec** — http://localhost:8080/api-docs
- **Health check** — http://localhost:8080/actuator/health
- **Prometheus metrics** — http://localhost:8080/actuator/prometheus

## Editing Code

### With Claude Code

Launch Claude Code from the project root:

```bash
claude
```

Claude Code has access to all project files and can run builds and tests. When working with Claude:

- Ask it to read files before making changes — it follows the project's hexagonal architecture and checkstyle rules automatically.
- It can run `./mvnw verify` to validate changes against all quality gates (checkstyle, tests, coverage).
- It creates commits following the project's conventions when asked.
- The project conventions are documented in `CLAUDE.md` at the project root.

### Manual editing

The project enforces these rules via Checkstyle (runs at the `validate` phase before compilation):

| Rule | Limit |
|------|-------|
| File length | 750 lines max |
| Method length | 30 lines max |
| Braces | Required on all blocks |
| Star imports | Forbidden |
| Unused imports | Forbidden |
| Redundant imports | Forbidden |

Architecture boundaries are enforced by ArchUnit tests — the domain layer must not depend on infrastructure or application layers. See [ADR-0002](doc/adr/0002-use-hexagonal-architecture.md) for details.

### Javadoc standard

All production classes require Javadoc with an embedded Mermaid diagram. See [ADR-0022](doc/adr/0022-javadoc-standard-with-mermaid-diagrams.md) for the full standard.

- **Models and ports** — `classDiagram`
- **Controllers and services** — `sequenceDiagram`
- **Configuration classes** — `flowchart`

Diagrams use `<div class="mermaid">` blocks in Javadoc comments and are rendered as interactive images by Mermaid JS in the generated documentation.

## Build and Test

### Compile only

```bash
./mvnw compile
```

### Run tests

```bash
./mvnw test
```

Requires the dev database running on port 5434. Tests include:
- **Unit tests** — JUnit 5 + Mockito, no Spring context
- **Integration tests** — `@SpringBootTest` + `@AutoConfigureMockMvc` against PostgreSQL
- **Architecture tests** — ArchUnit verifying hexagonal boundaries

All test methods require `@DisplayName`. Test method naming convention: `should<Expected>When<Condition>`.

### Full verification

```bash
./mvnw verify
```

Runs in order: checkstyle, compile, tests, JaCoCo coverage check (85% minimum line coverage), and javadoc generation.

### Test coverage report

```bash
./mvnw test jacoco:report
```

Opens the report at `target/site/jacoco/index.html`. The build fails if line coverage drops below 85%.

## Generate Javadoc

```bash
./mvnw package -DskipTests
```

Javadoc is generated during the `package` phase. Output is at `target/reports/apidocs/index.html`.

The generated documentation includes Mermaid diagrams that render in-browser — open the HTML files directly (or serve them) to see the diagrams. The `mermaid-init.js` script loads Mermaid v11 from CDN and renders all `<div class="mermaid">` blocks.

To generate javadoc independently:

```bash
./mvnw javadoc:javadoc
```

## Run Locally

### Dev profile (default)

```bash
./mvnw spring-boot:run
```

- PostgreSQL on `localhost:5434` (see [Environment Setup](#environment-setup))
- Form login: `dev` / `dev`
- Port: **8080**

### Prod profile locally (with OAuth)

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=prod \
  -Dspring-boot.run.arguments="--DB_URL=jdbc:postgresql://localhost:5434/mystuffdb --DB_USERNAME=postgres --DB_PASSWORD=test --GOOGLE_CLIENT_ID=your-id --GOOGLE_CLIENT_SECRET=your-secret"
```

## Deploy to Production

Production deployment uses Docker Compose to run the application, PostgreSQL, Prometheus, and Grafana.

### 1. Configure environment

Copy the example and fill in real values:

```bash
cp .env.example .env
```

Edit `.env`:

```
DB_PASSWORD=<strong-password>
GOOGLE_CLIENT_ID=<your-google-oauth-client-id>
GOOGLE_CLIENT_SECRET=<your-google-oauth-client-secret>
```

Google OAuth credentials are configured for the `prod` profile. Set up a project in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) with `email` and `profile` scopes.

### 2. Build and start

```bash
docker compose up -d --build
```

This starts four services:

| Service | Port | Description |
|---------|------|-------------|
| **app** | 8888 | Spring Boot application (prod profile) |
| **db** | 5436 | PostgreSQL 17 |
| **prometheus** | 9091 | Metrics collection (scrapes `/actuator/prometheus` every 15s) |
| **grafana** | 3000 | Dashboards (admin password: `admin`) |

The app waits for the database health check before starting. Flyway applies migrations automatically.

### 3. Rebuild after code changes

```bash
docker compose up -d --build app
```

### 4. View logs

```bash
docker compose logs -f app
```

### 5. Stop everything

```bash
docker compose down
```

Add `-v` to also remove database and Grafana volumes.

## Project Structure

```
src/main/java/solutions/mystuff/
  application/web/        # Controllers, PDF generators, input validation
  domain/
    model/                # Entities: Item, Vendor, ServiceSchedule, ServiceRecord, etc.
    port/in/              # Inbound ports (use-case interfaces)
    port/out/             # Outbound ports (repository interfaces)
    service/              # Domain service implementations
  infrastructure/
    config/               # Security, sample data, bean wiring
    correlation/          # Request correlation ID filter
    persistence/          # JPA repository adapters

src/main/resources/
  db/migration/           # Flyway SQL migrations
  templates/              # Thymeleaf templates
  static/                 # CSS, JavaScript, images

config/checkstyle/        # Checkstyle rules
doc/adr/                  # Architecture Decision Records
```

## Architecture Decisions

Decisions are documented as ADRs in `doc/adr/`. Key decisions:

- [ADR-0002](doc/adr/0002-use-hexagonal-architecture.md) — Hexagonal architecture with ArchUnit enforcement
- [ADR-0003](doc/adr/0003-use-spring-boot-4-with-thymeleaf-and-htmx.md) — Spring Boot 4 + Thymeleaf + HTMX
- [ADR-0006](doc/adr/0006-use-oauth2-for-production-and-form-login-for-development.md) — OAuth2 in prod, form login in dev
- [ADR-0008](doc/adr/0008-organization-based-multi-tenancy.md) — Organization-based multi-tenancy
- [ADR-0013](doc/adr/0013-use-checkstyle-for-code-quality-enforcement.md) — Checkstyle quality enforcement
- [ADR-0022](doc/adr/0022-javadoc-standard-with-mermaid-diagrams.md) — Javadoc standard with Mermaid diagrams

## Additional Documentation

- [Contributing Guide](CONTRIBUTING.md) — How to set up, make changes, and submit code
- [User Guide](doc/USER_GUIDE.md) — End-user documentation for using the application
- [CLAUDE.md](CLAUDE.md) — Conventions and rules for Claude Code
