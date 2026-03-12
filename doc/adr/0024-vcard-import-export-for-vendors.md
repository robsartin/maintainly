# 24. vCard import/export for vendors

Date: 2026-03-12

## Status

Accepted

## Context

Vendors are created one at a time through schedule and record forms. Users need to bulk-import contacts from existing databases and export for use in other tools. vCard 4.0 (RFC 6350) is the industry standard supported by all contact management applications.

## Decision

1. **Hand-rolled parser/serializer** — `VCardSerializer` and `VCardParser` are final utility classes in `domain.service`, following the `LogSanitizer` / `UuidV7` pattern. No external library dependency keeps the build lean and avoids supply chain risk.

2. **Field mapping** — Vendor fields map to vCard properties:

   | Vendor field | vCard property |
   |---|---|
   | name | FN |
   | phone | TEL;TYPE=work |
   | altPhones | TEL;TYPE={label} |
   | email | EMAIL |
   | address fields | ADR;TYPE=work (7-component) |
   | website | URL |
   | notes | NOTE |

3. **New Vendor fields** — `website` (VARCHAR 2000) and `notes` (VARCHAR 2000) added via Flyway migration V9.

4. **Hexagonal port** — `VendorImportExport` inbound port with `exportVendor`, `exportAllVendors`, and `importVendors` methods, implemented by `VendorImportExportService`.

5. **Import security measures**:
   - All fields sanitized via `LogSanitizer.sanitize()` (strips control characters)
   - All fields truncated to database column maximum lengths
   - Maximum 100 vCards per import file
   - 512 KB multipart upload limit (existing application config)
   - CSRF required on POST import endpoint (Spring Security default)
   - No raw imported data in error messages or logs

6. **Controller endpoints**:
   - `GET /vendors/export` — download all vendors as `vendors.vcf`
   - `GET /vendors/export/{id}` — download single vendor as `vendor.vcf`
   - `POST /vendors/import` — upload `.vcf` file, import vendors, redirect to settings

## Consequences

- Users can bulk-import contacts from any vCard-compatible tool (Apple Contacts, Google Contacts, Outlook, etc.).
- Exported vendor files can be shared with other team members or imported into external CRM tools.
- No new library dependencies reduce maintenance burden and supply chain attack surface.
- The 100-vendor import limit prevents denial-of-service via oversized files.
- `ControllerErrorAdvice` routes `/vendors` errors to the settings redirect, consistent with the settings page pattern.
