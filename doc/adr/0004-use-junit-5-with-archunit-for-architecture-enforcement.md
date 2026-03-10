# 4. Use JUnit 5 with ArchUnit for architecture enforcement

Date: 2026-03-09

## Status

Accepted

## Context

We need automated enforcement of hexagonal architecture boundaries and a consistent testing framework.

## Decision

- **JUnit 5** for all tests
- **ArchUnit 1.4.1** for architecture rule enforcement
- **Test-driven development**: write tests before production code
- **JaCoCo** enforces 80% minimum line coverage at bundle level
- Architecture tests run on every build and must never be skipped

### Test categories

| Category | Framework | Spring Context | Database |
|----------|-----------|---------------|----------|
| Architecture | ArchUnit + JUnit 5 | No | No |
| Unit | JUnit 5 + Mockito | No | No |
| Integration | Spring Boot Test + MockMvc | Yes | Docker PostgreSQL |

### Conventions

- Test class naming: `*Test.java` / `*IntegrationTest.java`
- Method naming: `should<Expected>When<Condition>`
- `@DisplayName` on all test classes and methods
- Integration tests use `user("dev").roles("USER")` and `csrf()`

## Consequences

- Architecture violations caught before code review
- TDD ensures high coverage from the start
- 80% coverage floor prevents untested code from shipping
