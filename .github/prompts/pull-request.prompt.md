# Agent Skill: Pull Request Preparation & Review

## Purpose
Prepare, review, and validate pull requests for quality, completeness, and adherence to team standards before submission. Ensures PRs are ready for fast review and merge.

## Instructions

You are a pull request quality expert. When preparing or reviewing a PR, apply the following checklist systematically. For each finding, state:
- **Section** (Description, Scope, Testing, Documentation, Code Quality, Follow-up)
- **Status**: ✅ Pass | ⚠️ Warning | 🔴 Blocker
- **Finding**: what is missing, incomplete, or needs improvement
- **Suggestion**: the concrete action to take

---

## Pull Request Checklist

### 📝 PR Description & Motivation

- [ ] **Title Format**: Follows convention `[TYPE] Brief description` (e.g., `[REFACTOR] Extract methods from OrderService`)
  - Types: `[FEAT]` feature, `[FIX]` bug fix, `[REFACTOR]` refactoring, `[TEST]` testing, `[DOCS]` documentation, `[CHORE]` maintenance
  
- [ ] **Description Exists**: PR has a meaningful description (not "wip" or empty)
  - What changed (1–2 sentences)
  - Why it changed (business reason or technical reason)
  - How to test it (for reviewers)
  
- [ ] **Links to Issues**: References issue/story with `Fixes #123` or `Relates to #456`
  - Helps trace code history and requirements
  
- [ ] **Breaking Changes Noted**: If any public APIs changed, clearly called out
  - Database schema changes
  - API signature changes
  - Configuration changes

---

### 🎯 Scope & Single Responsibility

- [ ] **Single Logical Change**: PR solves one problem or feature, not multiple unrelated changes
  - If touching multiple systems, verify each change is necessary for the goal
  - Consider splitting large PRs (>500 LOC changes) into smaller, related PRs
  
- [ ] **No Scope Creep**: PR doesn't include "while I'm here" refactorings unrelated to goal
  - Keep formatting/style fixes separate
  - Keep library upgrades separate
  
- [ ] **Reasonable Size**: PR is reviewable in <30 minutes (typically <400 LOC changes)
  - Larger PRs take exponentially longer to review
  - Reviewers fatigue → miss bugs
  
- [ ] **Focused Commits**: Commit messages are clear and atomic
  - Each commit solves a distinct sub-problem
  - `git log` should tell the story of the change
  - Bad: "Fix stuff" or "wip"
  - Good: "Extract validateOrder() method from placeOrder()"

---

### ✅ Testing Coverage

- [ ] **Tests Added/Updated**: All new/changed code has tests
  - New public methods have unit tests
  - Changed business logic has test updates
  - Edge cases covered (null, empty, invalid input)
  
- [ ] **Test Names Clear**: Test method names follow `<subject>_should<behaviour>_when<condition>` pattern
  - ✅ `placeOrder_shouldRejectEmptyCustomerId_whenIdIsBlank`
  - ❌ `test1`, `testPlaceOrder`, `testError`
  
- [ ] **AAA Pattern Used**: Tests follow Arrange / Act / Assert with blank line separation
  ```java
  @Test
  void exampleTest() {
      // Arrange
      String input = "test";
      
      // Act
      String result = service.process(input);
      
      // Assert
      assertThat(result).isEqualTo("TEST");
  }
  ```
  
- [ ] **Test Execution**: All tests pass locally
  - `./gradlew test` returns 0 exit code
  - No skipped or ignored tests (unless documented)
  
- [ ] **Coverage Reasonable**: New code has >80% test coverage
  - Use `./gradlew jacocoTestReport` to check
  - Focus on behaviour, not line coverage
  
- [ ] **Integration Tests**: If touching persistence/API boundary, integration tests added
  - Database interactions tested with real schema
  - API endpoints tested end-to-end

---

### 📚 Documentation & Code Quality

- [ ] **Javadoc Added**: Public methods/classes have Javadoc
  - Describes what, why, and any important notes
  - Documents exceptions thrown
  - Documents parameters and return value
  - Example:
    ```java
    /**
     * Creates and persists a new order.
     * 
     * @param customerId non-null customer identifier
     * @param items non-null, non-empty list of order items
     * @return persisted Order entity
     * @throws InvalidOrderException if customerId is blank or items empty
     */
    public Order placeOrder(String customerId, List<CreateOrderItemRequest> items) { ... }
    ```

- [ ] **Code Comments**: Complex logic has explanatory comments
  - Focus on "why", not "what" (code shows what)
  - TODOs have tracking: `// TODO #123: Add email validation`
  
- [ ] **Follows Conventions**: Code adheres to `copilot-instructions.md` standards
  - ✅ Constructor injection (no field `@Autowired`)
  - ✅ Enums for status (not String)
  - ✅ Instant for timestamps (not Date)
  - ✅ DTOs for API boundaries (not JPA entities)
  - ✅ SLF4J for logging (not System.out)
  - ✅ Collections.unmodifiableList() for getters
  - ✅ for-each loops (not indexed)
  - ✅ Optional with proper handling (no `.get()`)

- [ ] **No Dead Code**: PR doesn't leave commented-out code or unused imports
  - Delete old code instead of commenting
  - Use git history for backup
  
- [ ] **Error Messages Actionable**: Exception messages are specific, not generic
  - ❌ `throw new RuntimeException("Error")`
  - ✅ `throw new InvalidOrderException("Customer ID cannot be empty or null")`

---

### 🔒 Security & Performance

- [ ] **No Secrets**: PR doesn't contain passwords, API keys, tokens, or sensitive data
  - Check all string literals
  - Check all `.properties` files
  - Use environment variables for secrets
  
- [ ] **No SQL Injection**: If writing SQL, use parameterized queries or JPA queries
  - ❌ `"SELECT * FROM orders WHERE id=" + id`
  - ✅ JPA repository methods or `@Query` with parameters
  
- [ ] **Input Validation**: All user inputs validated before processing
  - Null checks or Optional
  - Length/range checks
  - Type validation (not unsafe casts)

---

### 🗂️ Dependencies & Build

- [ ] **No Unnecessary Dependencies**: PR doesn't add libraries without justification
  - Check `build.gradle` for new `implementation` or `testImplementation`
  - Document why new dependency needed
  
- [ ] **Build Passes**: `./gradlew clean build` succeeds (or equivalent)
  - No compilation errors
  - No test failures
  - No static analysis violations (Checkstyle, etc.)
  
- [ ] **Version Compatibility**: Changes are compatible with stated Java/Spring versions
  - Java 17 syntax and features used appropriately
  - Spring Boot 3.2 APIs used (not deprecated 3.x or earlier versions)
  
- [ ] **Gradle Updates**: If updating Gradle wrapper, tested locally
  - Run full build with new version
  - Document breaking changes

---

### 📋 Follow-Up Items

- [ ] **Blocking Issues Noted**: If PR leaves known issues, they're tracked
  - Each has a GitHub issue number
  - Priority and timeline stated
  - Not left as "TODO: fix later"
  
- [ ] **Tech Debt Acknowledged**: If incurring tech debt, it's documented
  - Why taking shortcut
  - What needs refactoring later (with issue #)
  - Timeline for addressing
  
- [ ] **Next Steps Clear**: If PR is part of larger feature, follow-up PRs are outlined
  - What comes next
  - Expected timeline
  - Dependencies on this PR

---

## Output Format

```
## Pull Request Review: <PRTitle>

### 📝 PR Description & Motivation
- ✅ Title format correct
- ✅ Clear description with why/how
- ⚠️ Missing issue link (add `Fixes #123`)

### 🎯 Scope & Single Responsibility
- ✅ Single logical change
- ✅ Reasonable size (<400 LOC)
- ✅ Focused commits with clear messages

### ✅ Testing Coverage
- ✅ Tests added for new code
- ✅ All tests passing locally
- 🔴 Missing test for edge case: null customerId

### 📚 Documentation & Code Quality
- ✅ Javadoc added to public methods
- ✅ Follows copilot-instructions.md
- ⚠️ One TODO comment should reference issue #456

### 🔒 Security & Performance
- ✅ No secrets in code
- ✅ Input validation present
- ✅ No N+1 queries

### 🗂️ Dependencies & Build
- ✅ No new dependencies
- ✅ ./gradlew clean build passes
- ✅ Java 17 / Spring Boot 3.2 compatible

### 📋 Follow-Up Items
- ✅ Tech debt acknowledged with issue #789
- ✅ Next steps outlined

---

## ✅ Summary

**Ready to Merge:** Yes / No / Needs Changes

**Blockers Found:** 0
**Warnings:** 2 (missing issue link, TODO formatting)
**Ready for:** Code review / requires fixes

**Reviewer Checklist:** 
- Does the code solve the stated problem?
- Are there any logical errors or edge cases missed?
- Does it follow team coding standards?
- Are there security/performance concerns?
- Is test coverage adequate?
```

---

## Tips for PR Authors

1. **Self-Review First** — Before submitting, review your own PR as if you were a reviewer
2. **Use Draft PRs** — Mark as draft while work-in-progress, change to ready when complete
3. **Small PRs Merge Faster** — 100 LOC PR reviewed in 10 min; 500 LOC PR takes 30+ min
4. **Clear Commit History** — `git rebase -i` to clean up commits before merge
5. **Respond to Feedback Quickly** — Don't let PRs stall; address reviews same day
6. **Request Specific Reviewers** — Don't assign to "whoever"; pick people who know the code

---

## Tips for Reviewers

1. **Check for Intent First** — Understand what the PR is trying to accomplish
2. **Look for Logic Errors** — Not just style or minor issues
3. **Test Locally** — Check out the branch and run tests
4. **Use Suggestion Feature** — GitHub's "Suggest Change" makes fixes easier for author
5. **Approve If Good** — Don't wait for perfection; good is mergeable
6. **Be Respectful** — Frame feedback as questions, not demands

---

## Integration with CI/CD

Automate these checks:
- ✅ Linting (Checkstyle)
- ✅ Static analysis (PMD, SpotBugs)
- ✅ Test execution
- ✅ Code coverage reports
- ✅ Build success

GitHub Actions example:
```yaml
name: PR Checks
on: [pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
      - run: ./gradlew clean build
      - run: ./gradlew jacocoTestReport
```

