# 18. Item detail view and schedule auto-advance

Date: 2026-03-10

## Status

Accepted

## Context

Users need to view service history and schedules for a specific item. When a scheduled service is completed, the next due date should automatically advance based on the schedule's frequency rather than requiring manual entry.

## Decision

### Item detail view

- Clicking an item name navigates to `GET /item/detail?itemId=...` which renders the dashboard with an expanded detail row for that item.
- The detail row shows two sub-tables: Service History (date, summary, details) and Schedules (service type, next due, frequency, last completed, status).
- The controller loads item-specific records via `ServiceRecordRepository.findByItemIdAndOrganizationId` and schedules via `ServiceScheduleRepository.findByItemIdAndOrganizationId`.

### Schedule auto-advance

- `ServiceSchedule.advanceNextDueDate(LocalDate)` computes the next due date by adding the frequency interval to the completed date.
- The method uses a switch expression over `FrequencyUnit` (days, weeks, months, years) to call the appropriate `LocalDate.plus*` method.
- `POST /schedule/log` now calls `advanceNextDueDate` instead of manually setting `lastCompletedDate`, keeping the schedule active with its next occurrence.
- Business logic lives in the domain model rather than the controller.

## Consequences

- Users can inspect an item's full service history and all schedules from the dashboard.
- Completing a scheduled service automatically sets the next due date, keeping the schedule in the upcoming service list.
- The `advanceNextDueDate` method is unit-testable independent of persistence.
- Schedules remain active after service completion, cycling through their frequency indefinitely.
