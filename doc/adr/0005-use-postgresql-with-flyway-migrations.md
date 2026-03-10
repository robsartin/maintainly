# 5. Use PostgreSQL with Flyway migrations

Date: 2026-03-09

## Status

Accepted

## Context

We need a reliable relational database with version-controlled schema migrations.

## Decision

- **PostgreSQL 17** for persistence
- **Flyway** for database migrations
- Test database: Docker PostgreSQL on port 5434
- Same engine in dev, test, and production (no H2)
- Migrations in `src/main/resources/db/migration`

## Consequences

- Schema changes are versioned and repeatable
- Tests run against the same engine as production
- Docker required for local development and CI
