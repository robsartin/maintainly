# 9. All data owned by organization for multi-tenancy isolation

Date: 2026-03-09

## Status

Accepted

## Context

The application uses organization-based multi-tenancy (ADR 0008). Every piece of data must be scoped to an organization so that users in one organization cannot see or modify data belonging to another. The domain model is expanding from simple properties/service requests to a richer model with items, vendors, service types, service schedules, and service records.

## Decision

Every domain entity table will include an `organization_id UUID NOT NULL` foreign key referencing the `organizations` table. All repository queries will be scoped by organization ID. No data may be accessed or created without an organization context.

Entities affected: vendors, vendor_alt_phones, service_types, items, service_schedules, service_records.

## Consequences

- Complete tenant isolation at the data level — no cross-organization data leakage possible via repository queries.
- All repository methods must accept organization ID as a parameter.
- Slightly more complex queries, but consistent and predictable access patterns.
- Indexes should include organization_id for query performance.
