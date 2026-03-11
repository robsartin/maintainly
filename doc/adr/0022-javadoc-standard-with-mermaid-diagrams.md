# 22. Javadoc standard with Mermaid diagrams

Date: 2026-03-10

## Status

Accepted

## Context

The codebase has grown to ~50 source files across a hexagonal architecture. New contributors need to quickly understand class purpose, relationships, and data flow without reading every line. Visual diagrams embedded in documentation accelerate onboarding and aid code navigation in IDEs.

## Decision

All production Java source files must have Javadoc following this standard. Test files are exempt.

### Class-level Javadoc (required on every class/interface/record/enum)

1. **Summary sentence** — one sentence describing what the class does and its role in the architecture.
2. **Mermaid diagram** — embedded in a `<pre>{@code ...}</pre>` block using Mermaid syntax. Diagram type chosen by class role:
   - **Domain model entities**: `classDiagram` showing the entity's fields and relationships to other entities.
   - **Port interfaces (in/out)**: `classDiagram` showing the interface methods and which service/adapter implements it.
   - **Domain services**: `sequenceDiagram` or `flowchart` showing the primary operation flow through ports.
   - **Controllers**: `sequenceDiagram` showing HTTP request → controller → service → repository flow.
   - **Infrastructure adapters**: `classDiagram` showing which port interface is implemented and delegation.
   - **Configuration classes**: `flowchart` showing what beans are created and their relationships.
   - **Utility/helper classes**: `classDiagram` showing public methods, or omit diagram if trivial (< 3 methods).
3. **`@see` tags** — references to related classes (ports, adapters, controllers that use this class).

### Method-level Javadoc

- **Public and package-private methods**: one-line `/** summary. */` unless the method has non-obvious behavior, parameters needing explanation, or throws exceptions.
- **Private methods**: no Javadoc (rely on descriptive naming).
- **Getters/setters**: no Javadoc (self-documenting).
- **`@param`**: include only when the parameter name alone is ambiguous.
- **`@return`**: include only when the return type alone is ambiguous.
- **`@throws`**: include for all checked and documented runtime exceptions.

### Format rules

- Mermaid blocks use `<pre>{@code ... }</pre>` so they render as plain text in standard Javadoc tools and as diagrams in Mermaid-aware renderers.
- Keep Javadoc concise — total class Javadoc should not exceed 30 lines including the diagram.
- Diagrams should have 3–8 nodes maximum; show only direct relationships, not the full system.
- Line-wrap Javadoc at the same column width as the project code style.

## Consequences

- Every class has a single-sentence purpose statement visible in IDE tooltips.
- Mermaid diagrams provide visual architecture maps without external tooling.
- The 750-line file limit (raised from 500 in ADR 13 to accommodate Javadoc) constrains verbosity.
- New contributors can understand class relationships from Javadoc alone.
- Diagrams must be updated when class relationships change, adding maintenance cost.
