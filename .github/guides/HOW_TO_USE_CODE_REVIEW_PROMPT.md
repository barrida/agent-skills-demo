# 🔍 How to Use Code Review Prompt

## Overview: When & Where to Use Code Review Skill

You'll use the Code Review skill in **2 key moments**:

```
1. BEFORE committing → Catch issues before code review starts
2. DURING code review → Systematic checklist for reviewers
```

---

## 📍 Scenario: Your Real Workflow

Let's walk through a real example with your OrderService.

### **Step 1: You've Finished Coding** ✅

```
Your code: OrderService.java
Status: Done, tests passing
Question: "Is this code ready for team review?"
Goal: Catch issues BEFORE pushing to GitHub
```

### **Step 2: Before Pushing**

**Use Code Review Skill to Self-Review (RECOMMENDED)** 🎯

Open Copilot Chat in IntelliJ and ask:

```
#file:.github/prompts/code-review.prompt.md

Please review this code:
#file:src/main/java/com/demo/service/OrderService.java
```

**Copilot Response:**

```
## Code Review: OrderService

### 🔴 Blockers
1. **Field Injection (Line 15)** — `@Autowired` on field violates constructor injection requirement.
   Finding: Field injection makes testing harder and hides dependencies.
   Suggestion: Replace with constructor injection.

2. **Unsafe Optional Unwrap (Lines 58, 65)** — `.get()` called without `isPresent()` check.
   Finding: Will throw `NoSuchElementException` instead of returning 404, causing 500 error.
   Suggestion: Use `.orElseThrow()` with explicit exception.

3. **Unchecked Map Casting (Lines 35-38)** — No null validation on Map values before casting.
   Finding: If `item.get("productId")` returns null or wrong type, ClassCastException thrown at runtime.
   Suggestion: Add null checks and type validation, or switch to typed `OrderItemDTO`.

### 🟠 Major Issues
1. **Generic RuntimeException (Lines 23, 26)** — Exceptions lack domain-specific meaning.
   Finding: API clients can't distinguish between validation errors and unexpected failures.
   Suggestion: Create custom exceptions: InvalidOrderException, OrderNotFoundException.

2. **God Method - placeOrder() (Lines 19-56)** — 38-line method mixing validation, item processing, calculation, notification.
   Finding: Violates SRP. Hard to test in isolation, hard to reuse logic.
   Suggestion: Extract: validatePlaceOrderRequest(), addItemsToOrder(), calculateTotal(), notifyCustomer().

3. **Business Logic on Public Fields (Lines 41, 44)** — Directly accessing order.items and order.total.
   Finding: Violates encapsulation. Order state can be corrupted without validation.
   Suggestion: Add domain methods on Order: addItem(), setTotal().

4. **System.out.println in Production (Lines 51-52)** — No logging, just raw stdout.
   Finding: Output can't be configured, redirected, or filtered.
   Suggestion: Use SLF4J logger: log.info("Sending email to customer: {}", customerId).

5. **Duplicate Total Calculation Logic (Lines 76-84 vs 37-43)** — Same math in two places.
   Finding: If calculation changes, both places must be updated.
   Suggestion: Extract into reusable calculateTotal(List<OrderItem>) method.

6. **Magic String Status (Lines 45, 68, 71)** — "PENDING", "SHIPPED", "CANCELLED" scattered as literals.
   Finding: Status is finite state set; using String is error-prone (typos, no IDE support).
   Suggestion: Create OrderStatus enum.

### 🟡 Minor Issues
1. **Indexed for-loop Instead of for-each (Lines 31, 80)** — `for (int i = 0; ...)` less readable.
   Finding: Verbose and error-prone.
   Suggestion: Replace with for-each: `for (Map<String, Object> item : items)`

2. **BigDecimal("0") Instead of ZERO (Line 30)** — Creates new instance unnecessarily.
   Finding: Uses more memory, slower than constant.
   Suggestion: Use `BigDecimal.ZERO`.

3. **items.size() == 0 Instead of isEmpty() (Line 25)** — Less idiomatic.
   Finding: `.isEmpty()` clearer intent.
   Suggestion: Use `if (items == null || items.isEmpty())`

4. **Abbreviated Parameter Name (Line 59)** — `cId` instead of `customerId`.
   Finding: Reduces readability.
   Suggestion: Use full name: `public List<Order> getOrdersByCustomer(String customerId)`

5. **Business Logic on String Equality (Lines 67-68)** — Status checks hardcoded in service.
   Finding: If status enum introduced, these string comparisons become incorrect.
   Suggestion: Move to OrderStatus enum with predicate: `if (!order.status.isCancellable())`

### 🔵 Nitpicks
1. **Inconsistent Formatting (Line 35)** — Mixed casting styles.
2. **Comment on Line 18** — Remove before merging.
3. **Missing Javadoc** — Public methods lack documentation.

### ✅ Summary
Total findings: 14 | Blockers: 3 | Major: 6 | Minor: 5 | Nitpicks: 3

**Overall verdict: Request Changes** 🚫

**Critical Path to Production-Ready:**
1. Replace field injection with constructor
2. Replace .get() with .orElseThrow()
3. Add null/type validation on Map casts
4. Introduce OrderStatus enum
5. Extract custom exception types
6. Split placeOrder() into focused methods
```

Then you know: ✅ **What needs fixing BEFORE team review!**

---

## 🔄 Real-World Timeline

```
2:00 PM - You finish coding OrderService
          All tests pass ✅

2:15 PM - Use Code Review skill
          Ask: "Please review this code"

2:16 PM - Copilot reviews (30 sec)

2:17 PM - Get detailed report with:
          - 3 Blockers (must fix)
          - 6 Major issues
          - 5 Minor issues
          - 3 Nitpicks
          - Overall verdict

2:20 PM - Read Blockers first
          Fix them locally

2:35 PM - git push (code is better now)

2:40 PM - Create PR on GitHub

2:45 PM - Team reviews
          Fewer issues to comment on
          Faster approval ✅
```

---

## 📋 You Have 2 Usage Patterns

### **Pattern 1: Self-Review Before Pushing** (Most Important)

**When:** You're about to push code to GitHub  
**Who:** You (code author)  
**What:** Catch issues before team sees them  
**Command:**
```
#file:.github/prompts/code-review.prompt.md

Please review this code:
#file:src/main/java/com/demo/service/OrderService.java
```

**Benefit:** Fix issues locally (fast), before team review slows you down

---

### **Pattern 2: Systematic Review During Code Review** (Optional)

**When:** Reviewer wants to use a systematic checklist  
**Who:** Code reviewer on the team  
**What:** Thorough, consistent review against checklist  
**Command:**
```
#file:.github/prompts/code-review.prompt.md

Systematically review this PR:
#file:src/main/java/com/demo/service/OrderService.java

Provide detailed findings by severity.
```

**Benefit:** Reviewer has structure; nothing missed

---

## 🎯 Recommended Workflow for Your Team

### **Step 1: Developer (Before Pushing)**
```
Write code ✅
Tests pass ✅
USE CODE REVIEW SKILL → Catch issues locally
Fix Blockers + Major
git push (improved code) ✅
```

### **Step 2: Create PR**
```
Create PR on GitHub
GitHub Actions run ✅
```

### **Step 3: Team Review (Optional)**
```
Reviewer can USE CODE REVIEW SKILL
For systematic validation
Approve quickly (fewer issues)
```

### **Step 4: Merge**
```
Merge to master ✅
```

---

## ✅ What Code Review Skill Checks

The checklist validates **6 major areas**:

### **1. Correctness**
- NPE risks from unchecked `.get()` on Optional
- Unchecked casts and unvalidated inputs
- Race conditions or thread-safety concerns
- Error propagation (not swallowed, not leaking details)

### **2. Design & Responsibility**
- Single, clearly named responsibility per class/method
- Business logic on domain objects (not scattered in services)
- Mixed concerns (validation + persistence + notification)
- Duplicate logic that should be extracted

### **3. Spring Best Practices**
- Constructor injection (no `@Autowired` on fields)
- JPA entities kept out of responses (use DTOs)
- Repository methods typed appropriately (enum, not String)
- Transactions scoped correctly (`@Transactional` at service boundary)

### **4. API & HTTP Design**
- Correct HTTP status codes (404 vs 500, 201 vs 200)
- REST resource paths and HTTP verbs semantically correct
- Request validation via `@Valid` + constraints, not manual checks

### **5. Code Quality**
- Dead code, unused imports, commented-out code
- Magic strings replaced with constants or enums
- SLF4J logging (no `System.out.println`)
- Collection getters return defensive copies or unmodifiable views

### **6. Test Coverage**
- Happy path and all distinct failure paths tested
- Tests follow Arrange / Act / Assert with blank line separation
- Assertions specific (`.isEqualByComparingTo` for BigDecimal, not `.isEqualTo`)

---

## 🚨 Severity Levels Explained

### **🔴 Blockers (Must Fix)**
- Correctness issues (NPE, unsafe casts)
- Security vulnerabilities
- Breaking changes
- Code won't work as intended

**Action:** Fix BEFORE pushing

---

### **🟠 Major Issues (Should Fix)**
- Violates team standards
- Design flaws that hurt maintainability
- Duplicate code or scattered logic
- Missing error handling

**Action:** Fix in this push or follow-up PR

---

### **🟡 Minor Issues (Nice to Fix)**
- Code style inconsistencies
- Naming clarity
- Test coverage gaps
- Optimization opportunities

**Action:** Fix if time permits

---

### **🔵 Nitpicks (Optional)**
- Formatting
- Comments
- Documentation gaps
- Small improvements

**Action:** Fix in next opportunity

---

## 💡 Pro Tips

### **Tip 1: Run Review Before Pushing**

Good:
```
Code done → Use Code Review skill → Fix issues → git push
```

Not ideal:
```
git push → Team finds issues → Fix → git push again
```

---

### **Tip 2: Focus on Blockers First**

When you get the report:
1. Fix all 🔴 Blockers (must fix)
2. Then fix 🟠 Major issues (should fix)
3. Then minor/nitpicks if time (optional)

---

### **Tip 3: Ask Clarifying Questions**

After review, you can ask:

```
"Show me how to refactor placeOrder() to fix the god method"

"What would the custom exception look like?"

"How do I add Javadoc to these methods?"
```

---

### **Tip 4: Compare with Code Quality Report**

Code Review finds:
- Correctness issues (will your code work?)
- Design issues (is it following standards?)

Code Quality finds:
- Smells (is it maintainable?)
- Anti-patterns (will it be hard to change?)

Use both for complete picture!

---

## 📊 Workflow: Review → Fix → Push → Merge

```
LOCAL DEVELOPMENT                  GITHUB                        MERGE
══════════════════════════════════════════════════════════════════════

Code done
Tests pass ✅
  ↓
USE CODE REVIEW SKILL ← ← → Systematic review
(Self-review)              Catches issues early
  ↓
Get report:
🔴 Blockers
🟠 Major
🟡 Minor
  ↓
Fix Blockers + Major
  ↓
git push ───────────→ Push improved code
                      ↓
                      Create PR
                      ↓
                      GitHub Actions ✅
                      ↓
                      Team reviews ────→ Quick (fewer issues)
                      (Optional: use review skill)
                      ↓
                      Approves ────────→ Merge ✅
```

---

## 🎓 Checklist: What Review Looks For

| Area | Checks |
|------|--------|
| **Correctness** | NPE risks? Exception handling? Thread safety? |
| **Design** | SRP? Business logic on domain? Duplicates? |
| **Spring** | Constructor injection? DTOs used? Transactions scoped? |
| **API** | HTTP status codes? REST semantics? Validation? |
| **Code Quality** | Logging? Magic strings? Defensive copies? |
| **Tests** | Happy + failure paths? AAA pattern? Specific assertions? |

---

## ✅ Summary

**You'll use Code Review Skill:**

1. **Before pushing** (catch issues locally)
2. **During code review** (systematic validation)
3. **To learn standards** (see what's expected)

**Benefits:**
- ✅ Fix issues before team review
- ✅ Faster PR approval process
- ✅ Consistent standards
- ✅ Learn best practices

**When You Finish Coding:**

1. Use Code Review skill → "Please review this code"
2. Get report with severity levels
3. Fix Blockers + Major issues
4. git push
5. Create PR
6. Team approves quickly
7. Merge to master ✅

---

## 🚀 Your Next Steps

1. **When you finish coding:**
   ```
   #file:.github/prompts/code-review.prompt.md
   
   Please review:
   #file:src/main/java/com/demo/service/YourService.java
   ```

2. **Get detailed report with severity levels**

3. **Fix Blockers first, then Major**

4. **git push improved code**

5. **Create PR on GitHub**

6. **Team reviews (faster now)** ✅

7. **Merge** 🎉

