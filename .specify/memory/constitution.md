<!--
Sync Impact Report
- Version change: (none) → 1.0.0 (initial ratification)
- Modified principles: n/a (template placeholders → concrete principles)
- Added principles:
  - I. MVVM with Unidirectional Data Flow
  - II. Layered Package Boundaries
  - III. Test-First for Business Logic (NON-NEGOTIABLE)
  - IV. Static Analysis Gate
  - V. Hilt-Managed Dependencies
- Added sections: Platform & Technology Constraints, Development Workflow, Governance
- Removed sections: none (template placeholders only)
- Templates requiring updates: none checked yet — plan/spec/tasks templates should be
  reviewed against these principles the next time they are resolved.
- Follow-up TODOs: none. All placeholders were resolved from repo inspection
  (app/build.gradle.kts, source tree under app/src/main/java/com/dailyexpense). If the
  team disagrees with any principle (e.g. Test-First given current low coverage),
  amend via /speckit-constitution.
-->

# DailyExpense Constitution

## Core Principles

### I. MVVM with Unidirectional Data Flow
Every screen's state MUST live in a ViewModel under `presentation/`, exposed as a single
observable state (e.g. `StateFlow`); Composables under `ui/` are stateless renderers that
emit user actions upward and MUST NOT mutate state directly. Business logic MUST NOT live
inside Composable functions.
Rationale: keeps UI testable independent of Compose rendering and keeps state transitions
traceable to one source per screen.

### II. Layered Package Boundaries
`ui/` MUST only depend on `presentation/` (ViewModels, UI state); `presentation/` MUST only
depend on `data/` through repository interfaces, never directly on Room DAOs or Retrofit
services; cross-cutting wiring lives in `di/`; `navigation/` contains routing only and MUST
NOT contain business logic.
Rationale: keeps layers independently replaceable and prevents Compose/Android framework
types from leaking into business logic.

### III. Test-First for Business Logic (NON-NEGOTIABLE)
ViewModels and repository/use-case logic MUST have JUnit tests written alongside or before
implementation; a feature's `/speckit-tasks` output MUST include test tasks for every
ViewModel and repository it introduces or changes. Pure UI/styling-only Composable changes
are exempt.
Rationale: the codebase currently has minimal automated test coverage; adopting
Spec-Driven Development is the point at which this is enforced going forward rather than
retrofitted later.

### IV. Static Analysis Gate
Detekt (`config/detekt/detekt.yml`) MUST pass with zero new findings before a feature is
considered complete; any suppression requires an inline comment explaining why the rule
doesn't apply.
Rationale: Detekt is already wired into the build (`app/build.gradle.kts`); this principle
makes passing it a completion gate rather than an optional check.

### V. Hilt-Managed Dependencies
All cross-cutting dependencies (Room database, Retrofit services, Timber trees,
repositories) MUST be provided through Hilt modules under `di/`; features MUST NOT
instantiate singletons or service locators manually.
Rationale: keeps dependency wiring consistent with the existing `di/` package and testable
via Hilt test modules.

## Platform & Technology Constraints

- Kotlin + Jetpack Compose only for new UI; no new XML layouts.
- minSdk 26, targetSdk/compileSdk 36, JVM target 17 — features MUST NOT require APIs above
  minSdk without a guarded runtime check.
- Local persistence via Room (`data/`); remote calls via Retrofit; DI via Hilt; logging via
  Timber; analytics via Firebase Analytics.
- Do not introduce a second dependency-injection, persistence, or networking library
  without amending this constitution first.

## Development Workflow

- New features go through Spec-Driven Development: `/speckit-specify` → `/speckit-plan` →
  `/speckit-tasks` → `/speckit-implement`, using `/speckit-clarify` whenever the spec has
  ambiguity before planning begins.
- Each feature is scoped to its own branch; commit messages describe the "why," not just
  the "what."
- A feature is not done until: Detekt passes, new business logic has tests, and the spec's
  acceptance criteria are satisfied. `/speckit-converge` may be used to verify this before
  merge.

## Governance

This constitution supersedes ad hoc conventions for any work initiated through Spec Kit.
Amendments are made via `/speckit-constitution` and MUST update the version per semantic
versioning: MAJOR for removed or redefined principles, MINOR for new principles or
materially expanded guidance, PATCH for wording or clarifications. `/speckit-plan` and
`/speckit-implement` MUST verify compliance with this document before proceeding;
unresolved conflicts are escalated to the user rather than silently resolved.

**Version**: 1.0.0 | **Ratified**: 2026-09-20 | **Last Amended**: 2026-09-20
