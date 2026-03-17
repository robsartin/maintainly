# 28. Require pull requests for all changes

Date: 2026-03-17

## Status

Accepted

## Context

Direct commits to `main` bypass CI checks and code review, increasing the risk of broken builds, regressions, and undocumented changes reaching production.

## Decision

1. **No direct commits to `main`** — all code changes must go through a pull request, including bug fixes, documentation updates, and CI workflow changes.
2. **Branch naming** — use descriptive branch names (e.g., `add-schedule-editing`, `fix-vendor-lazy-load`). No long-lived feature branches; keep PRs small and focused.
3. **CI must pass** — the GitHub Actions build (compile, checkstyle, tests, coverage) must succeed before merging.
4. **Merge strategy** — use merge commits (not squash or rebase) to preserve individual commit history in the PR.
5. **Branch protection** — enable branch protection on `main` in GitHub to enforce these rules.

## Consequences

- Every change has a CI-verified build before it reaches `main`.
- The commit history on `main` consists only of merge commits, making it easy to identify and revert entire features.
- Developers must create a branch before starting work, even for small changes.
- Emergency hotfixes still go through a PR, but the review can be expedited.
