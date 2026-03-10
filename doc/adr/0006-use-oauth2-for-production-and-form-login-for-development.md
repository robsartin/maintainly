# 6. Use OAuth2 for production and form login for development

Date: 2026-03-09

## Status

Accepted

## Context

We need authentication that is secure in production but easy to use during development.

## Decision

- **Production and test (`prod` profile)**: Google OAuth2 login
- **Development (default profile)**: Form login with `dev`/`dev` credentials
- Spring Security with profile-based `SecurityFilterChain` beans
- Default dev user belongs to "Test Org" organization

## Consequences

- No OAuth2 credentials needed for local development
- Production uses Google identity provider
- Profile switching controls which auth mechanism is active
