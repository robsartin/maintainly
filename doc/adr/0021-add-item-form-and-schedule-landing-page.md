# ADR 0021: Add Item Form and Schedules as Landing Page

## Status
Accepted

## Context
Users need the ability to add new items directly from the items page
without requiring database seeding or admin tools. The initial landing
page after login should show the most actionable information first.
Buttons across the application lacked consistent iconography.

## Decision
- Change the post-login landing page from items to schedules, since
  upcoming service is the most time-sensitive view.
- Add an "Add Item" button and inline form to the items page, with a
  POST endpoint at `/items/add` following the existing PRG pattern.
- Add SVG icons to all action buttons (Search, Add, Save, Create,
  Log, Schedule, Delete, Logout) for visual consistency.
- The add-item form uses `toggleForm()` from the shared layout JS,
  reusing the existing pattern for inline forms.

## Consequences
- New users see schedules first; if they have no schedules, the
  empty state prompts them to navigate to items.
- Items can be created through the UI without needing sample data.
- All buttons now have consistent icon + label styling via the
  existing `btn-icon` CSS class.
