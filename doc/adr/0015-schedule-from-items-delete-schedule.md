# 15. Schedule from items and delete schedule

Date: 2026-03-10

## Status

Accepted

## Context

Users need to create service schedules directly from items rather than from existing schedules. They also need a way to remove schedules they no longer need. The schedule creation form should capture frequency (interval and unit) so the schedule is fully configured at creation time.

## Decision

### Move schedule creation to items table

- Remove the Schedule button from the service schedule table rows.
- Add a Schedule button with calendar-plus icon to each item row.
- The inline form captures service type (dropdown), next due date, frequency interval, and frequency unit.
- New endpoint `POST /item/schedule` creates a `ServiceSchedule` linked to the item.
- Service types and frequency units are loaded as model attributes for the dropdowns.

### Add schedule deletion

- Add a Delete button with trash icon to each schedule row.
- `POST /schedule/delete` performs a soft delete by setting the schedule's `active` flag to `false`.
- Soft-deleted schedules no longer appear in the dashboard (filtered by `findActiveByOrganizationId`).

## Consequences

- Users create schedules from items with full frequency configuration upfront.
- Schedules can be removed without losing historical service records linked to them.
- The schedule table is simplified to show only Log and Delete actions.
- Service type and frequency unit data must be loaded on every dashboard render.
