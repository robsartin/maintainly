# 3. Use Spring Boot 4 with Thymeleaf and HTMX

Date: 2026-03-09

## Status

Accepted

## Context

We need a web framework that supports server-rendered HTML with progressive enhancement for interactive features.

## Decision

- **Spring Boot 4.0.3** (Spring Framework 7, Jakarta EE 11)
- **Java 25** as the target JDK
- **Thymeleaf** for server-side HTML templating
- **HTMX** for dynamic page updates without full reloads
- Controllers return Thymeleaf fragment names for HTMX requests and full page names for standard requests

## Consequences

- No separate frontend build pipeline
- HTMX requests receive HTML fragments; standard requests receive full pages
- Server-side rendering keeps the architecture simple
