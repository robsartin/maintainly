# 19. Pagination with Link headers

Date: 2026-03-10

## Status

Accepted

## Context

The items and service schedule lists can grow large. The client requested limiting display to 10 items per page with the ability to increase up to 50. Standard HTTP Link headers provide a RESTful way to communicate pagination state.

## Decision

### Pagination approach

- Both items and schedules default to 10 per page, configurable up to 50 via `itemSize`/`schedSize` query parameters.
- Page numbers are zero-based, passed via `itemPage`/`schedPage` parameters.
- `PageResult<T>` record in `domain.model` wraps page content with metadata (page, size, totalElements, totalPages, hasNext, hasPrevious), keeping Spring Data's `Page` out of the domain.
- Repository port interfaces accept `int page, int size` parameters and return `PageResult<T>`.

### Repository adapter pattern

- `SpringDataItemRepository` and `SpringDataScheduleRepository` are package-private Spring Data interfaces.
- `JpaItemRepositoryAdapter` and `JpaScheduleRepositoryAdapter` implement the domain port interfaces, delegating to Spring Data and converting `Page<T>` to `PageResult<T>`.
- This keeps Spring Data types out of the domain layer.

### Link HTTP headers

- The controller emits RFC 5988 `Link` headers with `rel="first"`, `rel="last"`, `rel="prev"`, and `rel="next"` for both items and schedules.
- Each link type uses its own parameter prefix (`item` or `sched`) to allow independent navigation.

### Database indexes

- `idx_items_org_name` on `items(organization_id, name)` supports sorted pagination.
- `idx_items_org_location` on `items(organization_id, LOWER(location))` supports search queries.
- `idx_schedules_org_active_due` on `service_schedules(organization_id, active, next_due_date)` supports active schedule pagination.

## Consequences

- Users see at most 10 items/schedules by default, reducing page load.
- Users can increase page size up to 50 via a dropdown control.
- API consumers can follow Link headers for programmatic navigation.
- The domain layer remains free of Spring Data dependencies.
- Pagination indexes ensure queries remain efficient as data grows.
