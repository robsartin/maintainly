# 29. CSS linting with Stylelint

Date: 2026-03-20

## Status

Accepted

## Context

Multiple PRs have introduced CSS syntax errors (unclosed braces) that broke styling across the application. These errors are invisible at compile time because CSS files are static resources — the Java build has no awareness of CSS validity. Broken CSS reaches production silently.

We need automated CSS validation that:

1. Catches syntax errors (missing braces, invalid properties) before merge
2. Enforces consistent formatting
3. Does not add Node.js to the Java build or Docker image
4. Aligns with the project convention of minimal dependencies

## Decision

1. **Use [Stylelint](https://stylelint.io/)** — the standard CSS linter. It is free, open-source, and widely adopted.
2. **Run via `npx` in CI only** — GitHub Actions runners have Node pre-installed. No `package.json`, no `node_modules`, no changes to `pom.xml` or `Dockerfile`. Developers can optionally run `npx stylelint` locally.
3. **Configuration** — a `.stylelintrc.json` at project root extending `stylelint-config-standard` with minimal overrides for our conventions (CSS custom properties, Thymeleaf-compatible selectors).
4. **CI enforcement** — add a `lint-css` step to `.github/workflows/maven.yml` that runs before the Maven build. Failures block the PR from merging.
5. **No Maven plugin** — `frontend-maven-plugin` would add Node to the build, increasing complexity and build time. The CI-only approach keeps the Java build purely Java.

## Consequences

- CSS syntax errors will be caught before merge, preventing the recurring unclosed-brace bugs.
- CI adds ~5 seconds for the stylelint step.
- Developers without Node installed can still build and test locally; CSS linting is enforced in CI.
- If Stylelint rules need updating, only `.stylelintrc.json` changes — no build system impact.
