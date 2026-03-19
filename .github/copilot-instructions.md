# GitHub Copilot Instructions

## Project Overview
This is a Spring Boot 3 / Java 17 backend service using Gradle, JPA, and REST APIs.
The codebase is intentionally seeded with common real-world issues to demonstrate agent-assisted improvement workflows.

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Data JPA (H2 for dev, PostgreSQL for prod)
- Gradle (Groovy DSL)
- Lombok
- JUnit 5 + AssertJ

## Code Conventions
- Use constructor injection, never field injection
- Prefer `Optional` and proper exception types over `.get()` on Optional
- Domain status fields must use `enum`, never raw `String`
- Use `Instant` for timestamps, not `java.util.Date`
- All public APIs must have request DTOs and response DTOs — never expose JPA entities directly
- Use `Collections.unmodifiableList()` or return a copy for collection getters
- Prefer `for-each` over indexed `for` loops
- No `System.out.println` in production code — use SLF4J
- Exception messages must be specific and actionable

## Testing Conventions
- Test class naming: `<Subject>Test`
- Use AssertJ for all assertions
- Each test should follow Arrange / Act / Assert structure with blank line separation
- Use `@DisplayName` for non-trivial test methods

## Agent Skill Files
Individual agent prompt files live in `.github/prompts/`:
- `code-review.prompt.md` — systematic review checklist
- `code-quality.prompt.md` — static analysis and smell detection
- `refactoring.prompt.md`  — structured refactoring patterns
