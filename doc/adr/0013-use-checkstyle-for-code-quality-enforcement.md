# 13. Use Checkstyle for code quality enforcement

Date: 2026-03-10

## Status

Accepted

## Context

Consistent code style and structural limits reduce cognitive load and improve maintainability. Automated enforcement prevents style drift across contributors.

## Decision

Use maven-checkstyle-plugin 3.6.0 with Checkstyle 10.25.0, running at the `validate` phase so violations fail the build before compilation.

### Rules

| Rule | Limit |
|------|-------|
| FileLength | 500 lines |
| MethodLength | 30 lines |
| NeedBraces | Required |
| AvoidStarImport | Forbidden |
| UnusedImports | Forbidden |
| RedundantImport | Forbidden |

Configuration: `config/checkstyle/checkstyle.xml`

## Consequences

- Style violations caught before code compiles, not during review.
- 30-line method limit encourages small, focused methods with clear responsibility.
- Star imports forbidden to maintain explicit dependency visibility.
