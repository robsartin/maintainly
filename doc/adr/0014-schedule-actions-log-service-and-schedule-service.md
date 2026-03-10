# 14. Inline service actions: Log and Schedule

Date: 2026-03-10

## Status

Accepted

## Context

Users need to record completed service visits and create follow-up schedules directly from the dashboard without navigating to separate pages.

## Decision

Add inline action buttons with expandable forms to both the items table and the service schedule table:

### Item rows

- **Log Service** (`POST /item/log`) — Creates a `ServiceRecord` for the item. Captures service date, summary, and optional technician name.

### Schedule rows

- **Log Service** (`POST /schedule/log`) — Creates a `ServiceRecord` linked to the schedule's item, service type, and schedule. Updates the schedule's `lastCompletedDate`.
- **Schedule Service** (`POST /schedule/new`) — Creates a new `ServiceSchedule` copying item, service type, vendor, and frequency from the source with a user-specified next due date.

### Shared implementation

Both item and schedule log endpoints share a common `saveRecord` helper in `ItemController` to eliminate record-creation duplication. Inline SVG icons distinguish button types (document icon for log, calendar-plus for schedule).

## Consequences

- Service history builds from both ad-hoc item logging and schedule-driven workflows.
- New schedules inherit configuration from existing ones, reducing data entry.
- All actions remain on the single-page dashboard.
- Shared record-creation logic prevents divergence between item and schedule logging.
