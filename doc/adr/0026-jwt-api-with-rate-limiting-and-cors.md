# 26. JWT API with rate limiting and CORS

Date: 2026-03-16

## Status

Accepted

## Context

The application uses session-based authentication (form login in dev, Google OAuth2 in prod) for its Thymeleaf server-rendered UI. There are no stateless REST API endpoints for programmatic access. External clients and future front-end SPAs need a JSON API secured with bearer tokens rather than browser sessions.

## Decision

1. **Dual security filter chains** — A new `@Order(1)` filter chain in `ApiSecurityConfiguration` handles `/api/**` with stateless JWT authentication. The existing session-based chain (no explicit order) continues to handle all other paths. The API chain disables CSRF and sessions, and returns 401 via `HttpStatusEntryPoint` for unauthenticated requests.

2. **JWT signing** — HMAC-SHA256 via `NimbusJwtEncoder`/`NimbusJwtDecoder` from `spring-boot-starter-oauth2-resource-server`. Secret configured via `app.jwt.secret` (env var `JWT_SECRET` in prod). Zero clock skew tolerance on the decoder to reject expired tokens immediately.

3. **Token endpoint** — `POST /api/auth/token` accepts `{"username","password"}`, authenticates via `AuthenticationManager`, and returns `{"token":"..."}`. The `ApiTokenService` port in `domain.port.in` keeps the hexagonal boundary clean; `ApiTokenServiceImpl` in infrastructure handles authentication and JWT generation.

4. **Header-based API versioning** — URLs use `/api/items` without a version segment. Future breaking changes will be versioned via an `Accept` or custom header (e.g. `Api-Version: 2`), avoiding URL proliferation and keeping resource paths stable.

5. **Rate limiting** — In-memory token bucket per client IP in `RateLimitFilter`. General API endpoints: `app.rate-limit.requests-per-second` (default 10). Token endpoint: `app.rate-limit.login-requests-per-second` (default 3). Publishes `RateLimitExceededEvent` for security auditing.

6. **CORS** — Configurable allowed origins via `app.cors.allowed-origins`. Defaults to localhost in dev; env var `CORS_ALLOWED_ORIGINS` in prod. Restricts methods to GET/POST/PUT/DELETE and headers to Authorization + Content-Type.

7. **Security event logging** — `SecurityEventListener` extended with listeners for `JwtTokenIssuedEvent` and `RateLimitExceededEvent`, sanitized via `LogSanitizer`.

8. **API error handling** — `ApiErrorAdvice` (`@RestControllerAdvice` with `@Order(1)`, scoped to the `api` package) returns JSON `ApiErrorResponse` records. `ControllerErrorAdvice` continues handling Thymeleaf views with programmatic status codes to avoid `@ResponseStatus`/redirect conflicts.

## Consequences

- External clients can authenticate with `POST /api/auth/token` and access API endpoints with `Authorization: Bearer <token>`.
- The existing Thymeleaf UI continues to work unchanged with session-based auth.
- Rate limiting prevents brute-force attacks on the token endpoint without external dependencies.
- CORS restrictions prevent unauthorized cross-origin API access.
- Header-based versioning keeps URLs clean and RESTful; no URL changes needed for minor API evolution.
- In-memory rate limiting state is lost on restart and not shared across instances; a distributed rate limiter (Redis, etc.) would be needed for horizontal scaling.
- `ControllerErrorAdvice` now sets HTTP status programmatically instead of using `@ResponseStatus` annotations, correctly handling both redirect and rendered-view error responses.
