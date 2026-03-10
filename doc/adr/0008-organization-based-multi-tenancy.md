# 8. Organization-based multi-tenancy

Date: 2026-03-09

## Status

Accepted

## Context

Users belong to organizations. All data queries must be scoped to the user's organization.

## Decision

- Organization is identified by an integer ID
- Each user belongs to exactly one organization
- Users without an organization cannot access any features beyond login
- Organization ID is included in all data queries
- A default "Test Org" (id=1) is seeded for development with the `dev` user

## Consequences

- Data isolation between organizations is enforced at the query level
- Users must be assigned to an organization before they can use the application
- The organization context is available throughout request processing
