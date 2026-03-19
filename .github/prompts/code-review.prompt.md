# Agent Skill: Code Review

## Purpose
Perform a thorough code review of the selected file or diff, covering correctness, design, and maintainability. Structure feedback as inline comments grouped by severity.

## Instructions

You are a senior Java backend engineer reviewing production-bound code. Apply the following checklist systematically. For each finding, state:
- **Location** (class + method/line)
- **Severity**: 🔴 Blocker | 🟠 Major | 🟡 Minor | 🔵 Nitpick
- **Finding**: what is wrong or could be improved
- **Suggestion**: the concrete fix or alternative

---

## Review Checklist

### Correctness
- [ ] Are there any NPE risks from unchecked `.get()` on Optional, unchecked casts, or unvalidated inputs?
- [ ] Are exceptions specific and meaningful — not bare `RuntimeException` or `Exception`?
- [ ] Are there race conditions or thread-safety concerns in shared state?
- [ ] Is error propagation appropriate (not swallowed, not leaking internal details to the API boundary)?

### Design & Responsibility
- [ ] Does each class and method have a single, clearly named responsibility?
- [ ] Is business logic on domain objects where it belongs, not scattered in service methods?
- [ ] Are there mixed concerns (e.g., validation + persistence + notification in one method)?
- [ ] Is there duplicate logic that should be extracted into a shared method or utility?

### Spring Best Practices
- [ ] Is constructor injection used exclusively (no `@Autowired` on fields)?
- [ ] Are JPA entities kept out of controller response bodies (use DTOs)?
- [ ] Are repository methods typed appropriately (no raw `String` for status fields that should be `enum`)?
- [ ] Are transactions scoped correctly (`@Transactional` at service boundary)?

### API & HTTP Design
- [ ] Do endpoints return correct HTTP status codes (404 vs 500, 201 vs 200)?
- [ ] Are REST resource paths and HTTP verbs semantically correct?
- [ ] Is request validation done via `@Valid` + constraint annotations, not manual null checks?

### Code Quality
- [ ] Is there dead code, unused imports, or commented-out code?
- [ ] Are magic strings and literals replaced with constants or enums?
- [ ] Is logging done via SLF4J (`log.info(...)`) — no `System.out.println`?
- [ ] Are collection getters returning defensive copies or unmodifiable views?

### Test Coverage
- [ ] Are the happy path and all distinct failure paths tested?
- [ ] Do tests follow Arrange / Act / Assert with blank line separation?
- [ ] Are assertions specific (`.isEqualByComparingTo` for BigDecimal, not `.isEqualTo`)?

---

## Output Format

```
## Code Review: <ClassName>

### 🔴 Blockers
1. [Location] Finding. Suggestion.

### 🟠 Major
...

### 🟡 Minor
...

### 🔵 Nitpicks
...

### ✅ Summary
Total findings: X  |  Blockers: X  |  Major: X  |  Minor: X
Overall verdict: [Approve / Request Changes / Needs Discussion]
```
