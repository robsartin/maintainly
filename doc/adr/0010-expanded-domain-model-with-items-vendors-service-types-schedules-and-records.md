# 10. Expanded domain model with items, vendors, service types, schedules, and records

Date: 2026-03-09

## Status

Accepted

Supersedes the original Property/ServiceRequest model.

## Context

The initial model used simple Property and ServiceRequest entities. Real-world property maintenance requires tracking equipment/items within properties, recurring service schedules, service history records, vendors, and normalized service types.

## Decision

Replace Property and ServiceRequest with a richer domain model:

- **Item** — a maintainable asset (HVAC unit, roof, appliance) with manufacturer, model, serial number, and location details.
- **Vendor** — a service provider with contact info and alternate phone numbers (VendorAltPhone).
- **ServiceType** — normalized catalog of service types (e.g., "HVAC_INSPECTION", "ROOF_REPAIR") with code and description.
- **ServiceSchedule** — recurring maintenance schedule linking an item to a service type, with frequency (days/weeks/months/years), due dates, and optional preferred vendor.
- **ServiceRecord** — completed service event linking item, service type, vendor, and optional schedule, with cost tracking.
- **BaseEntity** — mapped superclass providing UUID V7 id, createdAt, and updatedAt via JPA lifecycle callbacks.
- **FrequencyUnit** — enum for schedule frequency (days, weeks, months, years).

## Consequences

- Richer domain model supports real maintenance workflows.
- Service schedules enable automatic next-due-date calculation when service is completed.
- Normalized service types prevent free-text inconsistency.
- More entities and relationships to test and maintain.
- UI must be updated to work with the new model.
