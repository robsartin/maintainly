# 20. Separate items and schedules screens

Date: 2026-03-10

## Status

Accepted

## Context

Items and service schedules were displayed on a single page, making it cluttered as data grew. Separating them into distinct screens improves usability and allows each page to focus on its own concerns.

## Decision

### URL structure

- `GET /` redirects to `/items`.
- `GET /items` displays the paginated item list with search, inline log, schedule creation, and item detail.
- `GET /items/detail?itemId=...` shows the item detail expanded.
- `POST /items/log` and `POST /items/schedule` handle item actions, redirecting back to `/items`.
- `GET /schedules` displays the paginated upcoming service schedule list.
- `POST /schedules/log` and `POST /schedules/delete` handle schedule actions, redirecting back to `/schedules`.

### Controller separation

- `ItemController` handles all `/items/**` endpoints.
- `ScheduleController` handles all `/schedules/**` endpoints.
- `ControllerHelper` extracts shared logic: user resolution, MDC management, page size clamping, and service record creation.
- `ControllerErrorAdvice` (`@ControllerAdvice`) provides shared exception handling for date parse errors, illegal arguments, and unexpected runtime errors.
- `LinkHeaderBuilder` simplified to use `page`/`size` parameters with a base path instead of prefixed parameters.

### Template separation

- `layout.html` contains shared Thymeleaf fragments (head with scripts, header with navigation).
- `items.html` and `schedules.html` include the layout fragments via `th:replace`.
- Navigation links between Items and Schedules appear in the header.

### POST-Redirect-GET

- All POST actions now redirect to their respective GET pages instead of re-rendering inline, following the PRG pattern to prevent duplicate submissions on browser refresh.

## Consequences

- Each screen is focused and loads only its own data, improving performance.
- PRG pattern eliminates duplicate form submission on refresh.
- Shared logic in `ControllerHelper` and `ControllerErrorAdvice` eliminates duplication between controllers.
- Layout fragments ensure consistent header, navigation, and scripts across pages.
- Adding new screens in the future follows the same pattern.
