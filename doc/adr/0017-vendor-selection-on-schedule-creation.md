# 17. Vendor selection on schedule creation

Date: 2026-03-10

## Status

Accepted

## Context

When creating a service schedule from an item, users need to assign a preferred vendor. Rather than a free-text field, the system should let users pick from existing vendors in their organization or create a new vendor inline.

## Decision

### Vendor dropdown with inline creation

- The schedule creation form includes a vendor dropdown populated from the organization's existing vendors.
- A special "New vendor..." option reveals inline fields for vendor name and phone.
- The dropdown defaults to "None" for schedules without a preferred vendor.
- The `vendorId` parameter uses the sentinel value `__new__` to signal inline vendor creation.

### Controller changes

- `POST /item/schedule` accepts optional `vendorId`, `newVendorName`, and `newVendorPhone` parameters.
- A `resolveVendor` helper routes between selecting an existing vendor, creating a new one, or returning null.
- A `createVendor` helper validates the name and persists the new vendor.
- `VendorRepository` is injected into `ItemController` and vendors are loaded as a model attribute.

### Schedule table

- The Upcoming Service table now includes a Vendor column showing the preferred vendor name when set.

## Consequences

- Users can assign vendors without leaving the dashboard.
- New vendors are created with minimal required data (name); full details can be added later.
- The vendor list grows organically as users create schedules.
- All vendor selection logic is centralized in the controller's `resolveVendor` method.
