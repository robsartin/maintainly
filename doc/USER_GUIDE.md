# Maintainly User Guide

Maintainly helps you track property maintenance -- the items you own, the service they need, the vendors who do the work, and the history of what's been done.

## Logging In

- **Production**: Sign in with your Google account. Your administrator must have added you to an organization first.
- **Development**: Use the credentials `dev` / `dev` at the login page.

If you see "You are not assigned to an organization," contact your administrator.

## Navigation

The top navigation bar has five sections:

| Icon | Page | Purpose |
|------|------|---------|
| Box | **Items** | Things you maintain (appliances, equipment, systems) |
| Calendar | **Schedules** | Upcoming and overdue service |
| People | **Vendors** | Service providers and contractors |
| Document | **Reports** | PDF reports for printing or sharing |
| Gear | **Settings** | Organization logo and user profile image |

## Items

Items are the things you maintain -- an HVAC system, a water heater, a roof, etc.

### Adding an item

1. Click the **+** button next to the Items heading
2. Fill in the name (required) and optional details: location, manufacturer, model, and serial number
3. Click **Save**

### Viewing item details

Click any item row to expand it. The detail view shows:

- **Service History** -- past service records for this item
- **Active Schedules** -- recurring maintenance schedules with next due dates

### Logging a one-off service

For unscheduled work (repairs, inspections, etc.):

1. Click the wrench icon on the item row
2. Enter the date, summary of work done, and optionally select a vendor and technician
3. To use a vendor not yet in the system, select "+ New vendor..." from the dropdown
4. Click **Save**

### Creating a schedule from an item

1. Click the calendar+ icon on the item row
2. Enter the service type (e.g., "HVAC Inspection"), select a vendor, set the next due date, and choose a frequency (e.g., every 6 months)
3. Click **Create**

### Searching items

Use the search box at the top of the Items page to filter items by name.

### Pagination

When you have many items, use the Prev/Next links at the bottom to navigate pages. You can change the page size (10, 25, 50, or 100) using the dropdown.

## Schedules

The Schedules page shows all upcoming service, sorted by due date.

### Color coding

- **Red** -- overdue (past due date)
- **Yellow** -- due soon (within 2 weeks)
- **Green** -- on track

### Completing a scheduled service

1. Click the log icon on the schedule row
2. Enter the date, a summary of the work, and optionally a vendor and technician name
3. The vendor defaults to the schedule's preferred vendor
4. Click **Save**

Completing a service automatically advances the schedule's next due date based on its frequency.

### Skipping a scheduled service

Click the skip icon to advance the next due date without logging a service record. You'll be asked to confirm.

### Creating a new schedule

Click the calendar+ icon on any schedule row to create an additional schedule for that same item.

### Editing a schedule

1. Click the pencil icon on the schedule row
2. The edit form expands with the current values pre-filled
3. Change the service type, vendor, next due date, or frequency as needed
4. Click **Save**

### Deleting a schedule

Click the trash icon and confirm. This removes the schedule but does not delete past service records.

## Vendors

Vendors are the service providers and contractors you work with.

### Adding a vendor

1. Click the **+** button
2. Fill in the name (required) and optional contact details: phone, email, address, website, and notes
3. To add multiple phone numbers, click "Add phone" in the Additional Phones section
4. Click the checkmark to save

### Editing a vendor

Click the pencil icon on a vendor row to expand the edit form. Make your changes and click the checkmark to save.

### Deleting a vendor

Click the trash icon and confirm. Vendor records referenced by schedules or service records will be cleared from those references.

### Importing vendors (vCard)

1. Click the upload icon in the Vendors header
2. Select a `.vcf` file (vCard format) from your device
3. Click the checkmark to import

This is useful for importing contacts from your phone or email client.

### Exporting vendors (vCard)

- **Export one vendor**: Click the download icon on that vendor's row
- **Export all vendors**: Click the download icon in the Vendors header

Exported `.vcf` files can be imported into contacts apps, phones, or other systems.

## Reports

### Service Due Soon

Generates a PDF showing all overdue service and service coming due soon. In the first half of the month, the report covers through the end of the current month. In the second half, it extends through the end of the following month. Useful for planning and assigning work.

Click the **PDF** button to open the report in a new tab.

### Item Service History

Generates a PDF showing the complete service history and active schedules for a single item.

1. Select an item from the dropdown
2. Click **PDF** to open the report in a new tab

## Settings

### Organization Logo

Click the image area (or "Click to upload" placeholder) to select a PNG or JPEG image. The image is automatically resized to 128x128 pixels and displayed in the navigation header.

### User Profile Image

Click the image area to upload your profile photo. It appears in the navigation bar next to your username.

## REST API

Maintainly provides a stateless JWT-secured REST API for programmatic access.

### Authentication

1. `POST /api/auth/token` with `{"username":"...","password":"..."}` to obtain a JWT
2. Include the token as `Authorization: Bearer <token>` on subsequent requests

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/token` | Issue JWT (public) |
| GET | `/api/items` | List items (paginated) |
| GET | `/api/items/{id}` | Item detail |

Rate limiting applies to all API endpoints (10 req/sec general, 3 req/sec for token endpoint).

### Documentation

Interactive API documentation is available at:

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI spec**: `/api-docs`
