# 🔧 How to Use Refactoring Prompt

## Overview: When & Where to Use Refactoring Skill

You'll use the Refactoring skill in **1 key moment**:

```
AFTER identifying issues → Get fixed code with changelog
```

---

## 📍 Scenario: Your Real Workflow

Let's walk through a real example with your OrderService.

### **Step 1: You Know What's Wrong** ✅

```
You ran: Code Review + Code Quality skills
You got: List of issues and smells
You know: What to fix and what order
Status: Ready to refactor
Question: "How do I fix this code safely?"
Goal: Get refactored code that works
```

### **Step 2: Get Fixed Code**

**Use Refactoring Skill to Apply Patterns (RECOMMENDED)** 🎯

Open Copilot Chat in IntelliJ and ask:

```
#file:.github/prompts/refactoring.prompt.md

Refactor OrderService to fix these issues (in priority order):

Priority 1: Introduce OrderStatus enum
Priority 2: Extract validateRequest(), calculateTotal(), addItems(), notifyCustomer() methods
Priority 3: Replace List<Map<String, Object>> with OrderItemDTO
Priority 4: Replace field injection with constructor injection
Priority 5: Replace .get() with .orElseThrow()

Provide:
- Full refactored OrderService.java
- Changelog with refactoring names
- Files that need updates
- Test-safety notes

#file:src/main/java/com/demo/service/OrderService.java
```

**Copilot Response:**

```
## Refactoring: OrderService

### Applied Refactorings (in order applied)
1. **Extract Method** — extracted validatePlaceOrderRequest() from placeOrder() (lines 20–35)
2. **Extract Method** — extracted addItemsToOrder() from placeOrder() (lines 51–67)
3. **Extract Method** — extracted calculateTotal() from placeOrder() (lines 69–76)
4. **Extract Method** — extracted notifyCustomer() from placeOrder() (lines 78–86)
5. **Replace String with Enum** — introduced OrderStatus enum; updated all usages
6. **Replace Primitive Obsession** — introduced CreateOrderItemRequest record
7. **Replace Field Injection with Constructor Injection** — in OrderService and OrderController
8. **Replace Unsafe Optional** — .get() → .orElseThrow(OrderNotFoundException)
9. **Replace println with Logger** — System.out → SLF4J log.info()
10. **Replace Date with Instant** — updated Order.createdAt to java.time.Instant

### Refactored Code

```java
// Full OrderService.java (refactored - 90 lines instead of 91, but cleaner)
// ... (full refactored code here)
```

### Test-Safety Notes
- `validatePlaceOrderRequest()` logic moved, functionality identical
- `calculateTotal()` extracted, behavior unchanged (uses Stream API instead of loop)
- `addItemsToOrder()` extracts item processing, same logic
- `notifyCustomer()` moves to SLF4J, output format slightly different
- Status changed from String to enum, all comparisons updated
- No behavior changes beyond the above

### Files That Need Updates
1. **Order.java** — Now uses OrderStatus enum and Instant
2. **OrderRepository.java** — Method signature: findByStatus(OrderStatus)
3. **OrderController.java** — Uses CreateOrderRequest DTO
4. **OrderServiceTest.java** — Update status assertions, use enum and DTOs
5. **Tests** — May need to import new exception types

### Follow-up Recommendations
- Consider event-driven notifications instead of logging
- Create response DTOs (OrderResponse, OrderItemResponse)
- Add global exception handler for OrderNotFoundException (→ 404)
- Add input validation with @Valid and JSR-303 constraints
```

Then you get: ✅ **Full refactored code + changelog + what to update!**

---

## 🔄 Real-World Timeline

```
2:00 PM - Run Code Review skill
          Get: 14 issues (3 Blockers, 6 Major, 5 Minor)

2:05 PM - Run Code Quality skill
          Get: 12 smells with priorities

2:10 PM - Understand what to fix
          Top 3: String→Enum, Extract methods, DTO

2:15 PM - Use Refactoring skill
          Ask: "Fix these 5 priorities"

2:16 PM - Copilot refactors (1-2 min)

2:18 PM - Get:
          - Full refactored code
          - Changelog (each refactoring listed)
          - Files to update
          - Test notes

2:20 PM - Copy refactored code into your files

2:30 PM - Run tests
          All still passing ✅

2:45 PM - Commit
          Message: "refactor: extract methods and introduce OrderStatus enum"

3:00 PM - git push

3:05 PM - Create PR
          Faster approval (code already improved)
```

---

## 📋 What Refactoring Skill Does

The skill applies **safe, behaviour-preserving patterns**:

### **Extract Patterns**
- Extract Method — block of code with clear purpose
- Extract Class — group of fields/methods forming distinct responsibility
- Extract Interface — class used polymorphically
- Extract Variable — complex expression needing documentation
- Extract Constant — magic literal used more than once

### **Rename Patterns**
- Rename Method — name doesn't describe purpose
- Rename Variable — abbreviation, single-letter, or misleading
- Rename Parameter — parameter name doesn't reflect role

### **Move Patterns**
- Move Method — method uses more data from another class
- Move Field — field logically belongs elsewhere

### **Simplify Conditionals**
- Decompose Conditional — extract predicate methods
- Replace with Polymorphism — switch/if-else on type
- Introduce Guard Clause — nested conditionals; return early
- Replace with Optional chain — null-guard chains

### **Replace with Idiomatic Java**
- Replace for-loop with Stream — filter/map/reduce
- Replace String status with Enum — finite-state fields
- Replace Date with Instant — use java.time.*
- Replace field injection with Constructor Injection
- Replace `.get()` with `.orElseThrow()` — safe Optional unwrap

### **Object Model Improvements**
- Introduce Value Object — wrap primitives (Money, CustomerId)
- Introduce Parameter Object — long parameter list that recurs
- Replace Magic Number with Constant — naked numeric/string literal
- Encapsulate Collection — return unmodifiable or defensive copy

---

## 💡 Pro Tips

### **Tip 1: Ask for One Priority at a Time**

You can ask multiple times:

```
First ask:
"Introduce OrderStatus enum and update all usages"

Then ask:
"Extract methods from placeOrder()"

Then ask:
"Replace field injection with constructor injection"
```

Or ask all at once (more powerful):

```
"Refactor in priority order:
1. Enum
2. Extract methods
3. Constructor injection"
```

---

### **Tip 2: Copy-Paste Generated Code**

The refactoring skill generates full file content:

1. Copy from Copilot response
2. Paste into your IDE file
3. Your IDE auto-formats
4. Save
5. Run tests

---

### **Tip 3: Read the Changelog**

The skill provides a changelog like:

```
1. Extract Method — validatePlaceOrderRequest() (lines 20-35)
2. Replace String with Enum — OrderStatus (lines 45, 68, 71)
3. Replace Primitive Obsession — CreateOrderItemRequest (lines 35-38)
```

This helps you:
- Understand each change
- Review what was modified
- Update tests accordingly

---

### **Tip 4: Check Test-Safety Notes**

Always read the notes section:

```
Test-Safety Notes:
- validatePlaceOrderRequest() logic moved, functionality identical
- calculateTotal() extracted, behavior unchanged
- No behavior changes beyond the above
```

This tells you:
- What tests to update
- What behavior didn't change
- If manual testing needed

---

### **Tip 5: Use After Code Review**

**Workflow:**
1. Code Review skill → Find issues
2. Code Quality skill → Understand smells
3. Refactoring skill → Get fixed code
4. Run tests → Verify
5. Push and merge

**Don't:**
- Use refactoring blindly without understanding what's wrong
- Apply random refactorings

**Do:**
- Review issues first (Code Review + Quality skills)
- Refactor with purpose
- Have tests in place

---

## 🚨 When to Use Refactoring Skill Most

### **Always** ✅
- After Code Review identifies issues
- After Code Quality identifies smells
- When you need safe pattern application
- When you want to learn refactoring patterns

### **Not Needed** ❌
- When code is fine (no issues found)
- For simple bug fixes (not refactoring)
- For new feature coding (refactor later)

---

## 📊 Workflow: Review → Quality → Refactor → Test → Push

```
ANALYSIS PHASE                      IMPROVEMENT PHASE              VALIDATION
════════════════════════════════════════════════════════════════════════════

Code written
  ↓
Run Code Review skill
  ↓ (identify correctness/design issues)
Get: 14 issues (blockers, major, minor)
  ↓
Run Code Quality skill
  ↓ (identify smells and priorities)
Get: 12 smells ranked by priority
  ↓
                          Run Refactoring skill ← ← ← START HERE
                          ↓ (apply safe patterns)
                          Get: Full refactored code
                             + changelog
                             + test notes
                          ↓
                          Copy refactored code
                          ↓
                          Run tests
                          All pass? ✅
                          ↓
                          Commit
                          ↓
                          git push ──────→ Create PR
                                           ↓
                                           GitHub Actions ✅
                                           ↓
                                           Team approves
                                           ↓
                                           Merge ✅
```

---

## 🎓 Common Refactorings & When to Use

| Pattern | When | Result |
|---------|------|--------|
| **Extract Method** | Method >20 lines, multiple responsibilities | Smaller, testable methods |
| **Replace String Enum** | String for finite states (status, type) | Type-safe, IDE support |
| **Extract Class** | Class has 2+ responsibilities | Focused classes |
| **Introduce DTO** | Untyped Map or long parameter list | Type-safe, clear contracts |
| **Rename** | Unclear naming | Self-documenting code |
| **Move Method** | Method uses another class's data more | Better encapsulation |
| **Replace conditional Polymorphism** | Switch/if-else on type | Extensible design |
| **Extract constant** | Magic number/string repeated | Maintainable literals |

---

## ✅ Summary

**You'll use Refactoring Skill:**

1. **After identifying issues** (Code Review + Code Quality)
2. **To get fixed code** (full refactored file)
3. **To learn patterns** (see how refactoring works)

**Benefits:**
- ✅ Safe pattern application
- ✅ Full refactored code (not piecemeal instructions)
- ✅ Changelog showing each change
- ✅ Test-safety guidance
- ✅ Ready to run tests immediately

**Complete Workflow:**

1. Code Review skill → Find issues
2. Code Quality skill → Understand smells & priorities
3. Refactoring skill → "Fix these priorities"
4. Get full refactored code
5. Run tests ✅
6. Commit and push
7. Create PR
8. Faster approval ✅
9. Merge ✅

---

## 🚀 Your Next Steps

1. **Run Code Review + Code Quality skills first**

2. **Identify top 3 priorities to fix**

3. **Use Refactoring skill:**
   ```
   #file:.github/prompts/refactoring.prompt.md
   
   Refactor to fix these (priority order):
   1. (issue name)
   2. (issue name)
   3. (issue name)
   
   Provide full refactored code with changelog.
   #file:src/main/java/com/demo/service/YourService.java
   ```

4. **Get refactored code**

5. **Copy into your files**

6. **Run tests** ✅

7. **Commit and push** 🎉

