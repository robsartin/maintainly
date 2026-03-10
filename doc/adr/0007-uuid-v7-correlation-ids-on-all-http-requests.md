# 7. UUID V7 correlation IDs on all HTTP requests

Date: 2026-03-09

## Status

Accepted

## Context

We need request traceability across logs and service boundaries.

## Decision

- Every incoming HTTP request must carry a UUID V7 correlation ID
- If the request includes an `X-Correlation-Id` header, use it
- Otherwise, generate a new UUID V7
- Store the ID in a thread-local context accessible throughout request processing
- Include the correlation ID in all log output via MDC
- Return the correlation ID in the `X-Correlation-Id` response header

## Consequences

- All log entries for a request are traceable via a single ID
- UUID V7 provides time-ordered, unique identifiers
- External callers can pass their own correlation IDs for end-to-end tracing
