# 23. Security hardening

Date: 2026-03-12

## Status

Accepted

## Context

A security review of the application identified several hardening opportunities across the OWASP Top 10 categories:

- **A05 Security Misconfiguration**: No HTTP security response headers (X-Frame-Options, Content-Security-Policy, X-Content-Type-Options, Referrer-Policy). Actuator health details exposed publicly in production.
- **A09 Security Logging Failures**: Correlation IDs accepted without validation, enabling log injection. User-controlled search queries logged without sanitization. No audit trail for authentication events.
- **A03 Injection**: Query parameter in Link headers not URL-encoded, enabling header injection. Error handler reflected raw user input from DateTimeParseException.

## Decision

1. **Security response headers** — Configure in `SecurityConfiguration.configureCommon()`: X-Frame-Options DENY, Content-Security-Policy (`default-src 'self'`), X-Content-Type-Options nosniff, Referrer-Policy strict-origin-when-cross-origin. HSTS disabled at the application level (delegated to the reverse proxy in production).

2. **Correlation ID validation** — Validate incoming `X-Correlation-Id` headers against UUID format in `CorrelationIdFilter`. Reject non-conforming values and generate a new UUIDv7, preventing log injection via forged correlation IDs.

3. **Log sanitization** — Introduce `LogSanitizer` in `domain.model` to strip newlines and control characters from user-supplied values before logging. Applied in `ItemController` (search query) and `ControllerHelper` (extracted username).

4. **Security audit logging** — Add `SecurityEventListener` in `infrastructure.config` to log authentication success and failure events using Spring's `ApplicationEvent` system, providing an audit trail for security investigations.

5. **Link header encoding** — URL-encode the query parameter in `LinkHeaderBuilder` to prevent header injection via crafted search terms.

6. **Error message sanitization** — Replace raw `parsedString` exposure in `ControllerErrorAdvice` DateTimeParseException handler with a generic error message, preventing reflected input in error pages.

7. **Actuator restriction** — Set `show-details: when-authorized` in the prod profile to prevent information disclosure of database connection details and system internals via the health endpoint.

## Consequences

- Security headers protect against clickjacking (X-Frame-Options), MIME sniffing (X-Content-Type-Options), and cross-site attacks (CSP, Referrer-Policy).
- Log injection attacks via correlation IDs and search queries are neutralized.
- Authentication events provide an audit trail visible in application logs.
- Error messages no longer risk reflecting attacker-controlled input.
- Production health details require authentication, reducing information disclosure.
- `LogSanitizer` in `domain.model` follows the existing utility pattern established by `UuidV7`.
- The CSP policy uses `'unsafe-inline'` for styles to support Thymeleaf's inline style attributes; this may be tightened with nonces in a future iteration.
