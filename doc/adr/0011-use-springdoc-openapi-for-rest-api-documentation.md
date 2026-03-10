# 11. Use SpringDoc OpenAPI for REST API documentation

Date: 2026-03-09

## Status

Accepted

## Context

The application needs API documentation for REST endpoints. SpringDoc OpenAPI integrates with Spring Boot to auto-generate OpenAPI 3.0 specs and provide Swagger UI.

## Decision

Use springdoc-openapi-starter-webmvc-ui 2.8.8 for automatic API documentation. Swagger UI available at /swagger-ui.html, OpenAPI spec at /api-docs. These endpoints are publicly accessible (no authentication required).

## Consequences

- Auto-generated API documentation with zero manual spec maintenance.
- Swagger UI enables interactive API testing during development.
- Public endpoints must be included in security configuration permit list.
