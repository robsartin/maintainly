# 16. Organization name and logo display

Date: 2026-03-10

## Status

Accepted

## Context

Users need to see their organization's identity when using the application. Displaying the organization name and logo provides branding and helps users confirm they are working in the correct organization context.

## Decision

### Organization model changes

- Add `logoUrl` field (VARCHAR 512, nullable) to the `Organization` entity for storing a URL to the organization's logo image.
- Logo is expected to be 128x128 pixels, displayed at 48x48 in the header for crisp rendering on retina displays.
- Create Flyway migration V5 to add the `logo_url` column to the `organizations` table.

### Dashboard display

- The header shows the organization logo (if set) alongside the application name.
- The organization name appears below the application title.
- The `organization` object is passed as a model attribute on all authenticated dashboard renders.
- Logo and name display gracefully degrade when not set (conditionally rendered with `th:if`).

## Consequences

- Every authenticated page displays the organization's branding.
- Organizations without a logo configured show the header without an image.
- The logo URL supports both local paths and external URLs.
- The 512-character limit accommodates long CDN or cloud storage URLs.
