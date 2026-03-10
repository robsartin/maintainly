# 12. Use Prometheus via Micrometer for metrics

Date: 2026-03-09

## Status

Accepted

## Context

Production monitoring requires application metrics (JVM, HTTP, database). Spring Boot Actuator with Micrometer provides a vendor-neutral metrics facade, and Prometheus is a widely adopted metrics backend.

## Decision

Use micrometer-registry-prometheus to expose application metrics at /actuator/prometheus in Prometheus exposition format. Exposed actuator endpoints: health, info, prometheus.

## Consequences

- Standard Prometheus scraping enables Grafana dashboards and alerting.
- Minimal configuration — Spring Boot auto-configures JVM, HTTP, and datasource metrics.
- Prometheus endpoint is publicly accessible for scraper access.
