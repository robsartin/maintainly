# 2. Use hexagonal architecture

Date: 2026-03-09

## Status

Accepted

## Context

We need a clear separation between business logic and infrastructure concerns to keep the codebase testable and adaptable.

## Decision

Adopt hexagonal (ports & adapters) architecture with these packages:

| Package | Responsibility |
|---------|---------------|
| `domain.model` | Entities, value objects, enums |
| `domain.port.in` | Inbound ports (use-case interfaces) |
| `domain.port.out` | Outbound ports (repository interfaces) |
| `domain.service` | Domain services implementing inbound ports |
| `application.web` | Controllers, view helpers (driving adapters) |
| `infrastructure.config` | Spring configuration beans |
| `infrastructure.persistence` | JPA repository implementations (driven adapters) |
| `infrastructure.correlation` | Correlation ID filter and context |

### Rules enforced by ArchUnit

- Domain must not depend on application or infrastructure
- Application may depend on domain but not infrastructure
- Infrastructure may depend on domain but not application (except config wiring)

## Consequences

- Domain logic is testable without Spring context
- Infrastructure can be swapped without touching domain
- ArchUnit tests catch violations on every build
