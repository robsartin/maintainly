# 27. User documentation kept current with changes

Date: 2026-03-16

## Status

Accepted

## Context

Feature changes and new capabilities have been added without corresponding updates to the user guide, leaving users to discover functionality through trial and error. Documentation drift erodes trust and increases support burden.

## Decision

Every commit that adds, removes, or changes user-facing behavior must include a corresponding update to `doc/USER_GUIDE.md`. This applies to:

- New UI features (buttons, forms, pages)
- Changed workflows (different steps, renamed actions)
- New or changed API endpoints
- Removed functionality

The user guide is the single source of truth for end-user documentation. Developer documentation (README, CONTRIBUTING, ADRs) is separate and follows its own update cadence.

## Consequences

- Users can rely on the guide being accurate for the current version.
- Pull request reviewers should check for user guide updates when UI or API changes are present.
- The guide grows incrementally with the application rather than requiring large catch-up rewrites.
