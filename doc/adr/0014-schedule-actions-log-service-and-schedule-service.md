# 14. Schedule actions: Log Service and Schedule Service

Date: 2026-03-10

## Status

Accepted

## Context

Users need to record completed service visits and create follow-up schedules directly from the service schedule view without navigating to separate pages.

## Decision

Add two inline actions to each service schedule row:

- **Log Service** (`POST /schedule/log`) — Creates a `ServiceRecord` linked to the schedule's item, service type, and schedule. Captures service date, summary, and optional technician name. Updates the schedule's `lastCompletedDate`.
- **Schedule Service** (`POST /schedule/new`) — Creates a new `ServiceSchedule` copying the item, service type, vendor, and frequency from the source schedule with a user-specified next due date.

Both actions use inline expandable forms toggled via JavaScript, keeping the user on the main page.

## Consequences

- Service history builds naturally from schedule-driven workflows.
- New schedules inherit configuration from existing ones, reducing data entry.
- All actions remain on the single-page dashboard — no separate CRUD pages needed yet.
