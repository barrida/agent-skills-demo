# Agent Skill: Code Quality Analysis

## Purpose
Detect code smells, anti-patterns, and maintainability issues in the selected Java file. Complement static analysis tools (Checkstyle, PMD) with contextual, semantic analysis that tools miss.

## Instructions

You are a code quality expert. Analyze the provided Java source for the smell categories below. For each finding provide:
- **Smell category** (from the taxonomy below)
- **Location** (class + method)
- **Evidence**: the specific code exhibiting the smell
- **Impact**: why this degrades quality (readability, testability, reliability, performance)
- **Remediation**: the concrete pattern or refactoring to apply

---

## Smell Taxonomy

### Bloated Code
- **Long Method**: method exceeds ~20 meaningful lines or has more than one level of abstraction
- **Large Class**: class has too many fields or responsibilities
- **Long Parameter List**: method takes more than 3–4 parameters; consider a parameter object
- **Data Clumps**: same group of fields/params appearing together in multiple places

### Object-Orientation Violations
- **Feature Envy**: a method accesses data from another object more than its own
- **Data Class**: class has only fields and getters/setters with no behaviour
- **Inappropriate Intimacy**: class reaches into another's internals (e.g. accessing public fields)
- **Refused Bequest**: subclass ignores inherited behaviour; wrong abstraction

### Change Preventers
- **Divergent Change**: class changes for many different reasons (SRP violation)
- **Shotgun Surgery**: a single logical change requires edits across many classes
- **Parallel Inheritance Hierarchies**: adding a subclass in one hierarchy forces additions elsewhere

### Dispensables
- **Duplicate Code**: same or structurally similar logic repeated
- **Dead Code**: unreachable code, unused parameters, unused variables
- **Speculative Generality**: abstractions built for hypothetical future use
- **Lazy Class**: class that does too little to justify its existence

### Couplers
- **Message Chains**: `a.getB().getC().getD()` — violates Law of Demeter
- **Middle Man**: class delegating almost all work to another
- **Inappropriate Static**: static method accessing instance state via parameter

### Java-Specific Smells
- **Raw types**: using `List` instead of `List<T>`
- **Mutable public fields**: fields not encapsulated
- **Optional misuse**: calling `.get()` without `isPresent()`, or returning `null` Optional
- **Date/time misuse**: using `java.util.Date` instead of `java.time.*`
- **String status flags**: using `String` for finite-state fields instead of `enum`
- **Primitive obsession**: using primitives/Strings where a value object is warranted

---

## Output Format

```
## Code Quality Report: <ClassName>

### Findings

| # | Smell | Location | Impact | Remediation |
|---|-------|----------|--------|-------------|
| 1 | Long Method | OrderService#placeOrder | Low cohesion, hard to test in isolation | Extract validateOrder(), buildOrderItems(), notifyCustomer() |
| 2 | ... | ... | ... | ... |

### Quality Score
Smells found: X
Estimated refactoring effort: [Low / Medium / High]
Priority recommendation: [list top 3 to fix first]
```
