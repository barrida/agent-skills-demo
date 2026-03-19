# 🐛 How to Use Code Quality Prompt

## Overview: When & Where to Use Code Quality Skill

You'll use the Code Quality skill in **2 key moments**:

```
1. DURING development → Understand what's wrong with your code
2. BEFORE refactoring → Get a prioritized roadmap of what to fix
```

---

## 📍 Scenario: Your Real Workflow

Let's walk through a real example with your OrderService.

### **Step 1: You've Written Code** ✅

```
Your code: OrderService.java
Status: Works fine, but feels messy
Tests: All passing ✅
Build: Success ✅
Question: "What's actually wrong with this code?"
```

### **Step 2: Before Refactoring**

**Use Code Quality Skill to Understand Issues (RECOMMENDED)** 🎯

Open Copilot Chat in IntelliJ and ask:

```
#file:.github/prompts/code-quality.prompt.md

Analyze this file for code smells:
#file:src/main/java/com/demo/service/OrderService.java
```

**Copilot Response:**

```
## Code Quality Report: OrderService

### Findings

| # | Smell | Location | Impact | Remediation |
|---|-------|----------|--------|-------------|
| 1 | Long Method | placeOrder() (lines 19–56) | 38 lines, multiple responsibilities, hard to test in isolation | Extract: validatePlaceOrderRequest(), addItemsToOrder(), calculateTotal(), notifyCustomer() |
| 2 | Primitive Obsession | placeOrder() param: List<Map<String, Object>> items | Type info lost, unsafe casts, no IDE refactoring support | Replace with List<CreateOrderItemRequest> DTO |
| 3 | Duplicate Code | placeOrder() lines 37–43 vs recalculateTotal() lines 80–84 | Both calculate: total += item.price * item.qty | Extract shared calculateTotal(List<OrderItem>) method |
| 4 | Feature Envy | placeOrder() accessing order.items, order.total | Should encapsulate in Order domain class | Move to Order: addItem(), setTotal() |
| 5 | String Status Flags | order.status = "PENDING" (lines 45, 68, 71) | Typos undetected, no enum IDE support, brittle comparisons | Replace with OrderStatus enum: PENDING, SHIPPED, DELIVERED, CANCELLED |
| 6 | Inappropriate Intimacy | cancelOrder() line 68: checking order.status equals strings | Status validation scattered in service, not encapsulated | Move to enum: order.status.isCancellable() |
| 7 | Data Clumps | Lines 35–38 repeat: (String) item.get("key"), (Integer) item.get("qty") | Same casting pattern repeats, error-prone | Extract toOrderItemDTO(Map<String, Object>) helper method |
| 8 | Raw Types / Unchecked Casts | (String), (Integer), (List<Map<...>>) casts without validation | ClassCastException at runtime if types mismatch, no compile-time safety | Use typed DTOs instead of raw Map |
| 9 | Optional Misuse | getOrder() line 58, cancelOrder() line 65: .get() without isPresent() | NoSuchElementException thrown, becomes 500 error instead of 404 | Replace with .orElseThrow(() -> new OrderNotFoundException(...)) |
| 10 | Weak Semantics | getOrdersByCustomer() param: String cId (abbreviated) | Inconsistent naming, unclear intent vs customerId elsewhere | Rename to customerId for consistency |
| 11 | Message Chains | recalculateTotal() lines 81–82: order.items.get(i).getPrice().multiply(...) | Law of Demeter violation, tight coupling to OrderItem internals | Extract orderItem.calculateLineTotal() method on domain |
| 12 | Lazy Class | OrderService + OrderRepository thin facade pattern | Minimal transformation logic, delegates almost everything | Consider consolidating or adding more responsibilities |

### Quality Score
Smells found: 12
Estimated refactoring effort: 🔴 **High** (requires structural changes)

**Priority recommendation (fix in this order):**

1. **String Status → OrderStatus Enum** (Medium effort, ~1–1.5 hrs)
   - Why first: Unblocks smells #5, #6; prevents future typo bugs
   - Action: Create OrderStatus enum, update all usages in Order/Service
   
2. **Extract Methods from placeOrder()** (High effort, ~1.5–2 hrs)
   - Why second: Fixes Long Method (#1), Feature Envy (#4), enables reuse
   - Action: Extract validateRequest(), addItems(), calculateTotal(), notifyCustomer()
   
3. **Introduce OrderItemDTO** (Medium effort, ~1 hr)
   - Why third: Fixes Primitive Obsession (#2), Data Clumps (#7), Raw Types (#8)
   - Action: Create CreateOrderItemRequest record with validation
```

Then you know: ✅ **Exactly what's wrong and where to start fixing it!**

---

## 🔄 Real-World Timeline

```
2:00 PM - You finish coding OrderService
          Code works, but feels complicated

2:15 PM - Use Code Quality skill
          Ask: "Analyze for code smells"

2:16 PM - Copilot analyzes (30 sec)

2:17 PM - Get detailed report with:
          - 12 smells identified
          - Location in code
          - Impact on quality
          - How to fix each one
          - Priorities ranked

2:20 PM - Read report
          Understand top 3 smells
          Plan your refactoring order

2:30 PM - Start fixing (guided by the report)
          OR use Refactoring skill to auto-fix

2:40 PM - All tests still passing ✅

3:00 PM - Code quality improved!
          Ready for PR ✅
```

---

## 📋 You Have 2 Usage Patterns

### **Pattern 1: Understanding Issues** (During development)

**When:** Code works but feels "off"  
**Who:** You (code author)  
**What:** Identify smells, understand priorities  
**Command:**
```
#file:.github/prompts/code-quality.prompt.md

Analyze this file for code smells:
#file:src/main/java/com/demo/service/OrderService.java
```

**Benefit:** Learn what's wrong and why; get priorities without guessing

---

### **Pattern 2: Planning Refactoring** (Before improvements)

**When:** You know code needs improvement, want a clear plan  
**Who:** You or your team lead  
**What:** Map out what to fix and in what priority order  
**Command:**
```
#file:.github/prompts/code-quality.prompt.md

Analyze this file. Focus on:
- Smells blocking testability
- Smells causing code duplication
- Highest impact fixes

#file:src/main/java/com/demo/service/OrderService.java
```

**Benefit:** Clear roadmap; don't fix randomly; fix highest-impact issues first

---

## 🎯 Recommended Workflow for Your Team

### **Step 1: Developer (During Coding)**
```
Write code ✅
Tests pass ✅
Code works, but feels messy...
USE CODE QUALITY SKILL → Understand issues
Get priorities → Plan refactoring
```

### **Step 2: Developer (Refactoring)**
```
Review priorities from Code Quality report
USE REFACTORING SKILL → Get fixed code
Run tests ✅
Commit improved code
```

### **Step 3: Push & Create PR**
```
git push
Create PR
Team reviews (faster, better code)
Merge ✅
```

---

## 🐛 Code Smells It Detects

### **Bloated Code**
- **Long Method**: >20 lines, doing multiple things
- **Large Class**: Too many fields or responsibilities
- **Long Parameter List**: >3–4 parameters

### **Object-Orientation Violations**
- **Feature Envy**: Method uses another object's data more than its own
- **Data Class**: Only getters/setters, no behavior
- **Inappropriate Intimacy**: Direct access to public fields

### **Change Preventers**
- **Duplicate Code**: Same logic in 2+ places
- **Divergent Change**: Class changes for many different reasons
- **Shotgun Surgery**: One logical change requires edits everywhere

### **Dispensables**
- **Dead Code**: Unreachable code, unused variables
- **Lazy Class**: Class does too little to justify existence
- **Speculative Generality**: Built for hypothetical future use

### **Couplers**
- **Message Chains**: a.getB().getC().getD() violates Law of Demeter
- **Middle Man**: Class delegates almost everything
- **Feature Envy**: Accessing another object's data excessively

### **Java-Specific**
- **String Status Flags**: Using String instead of Enum for states
- **Date Misuse**: Using java.util.Date instead of Instant
- **Optional Misuse**: Calling .get() without isPresent() check
- **Mutable Public Fields**: No encapsulation
- **Raw Types**: List instead of List<T>
- **Primitive Obsession**: Using primitives/Strings where value object warranted

---

## 💡 Pro Tips

### **Tip 1: Analyze One File at a Time**

Good:
```
#file:src/main/java/com/demo/service/OrderService.java
```

Not recommended:
```
(analyzing whole codebase at once - too much output)
```

---

### **Tip 2: Ask for Clarification**

After getting report, you can ask:

```
"Which 3 smells should I fix first?"

"What would the OrderStatus enum look like?"

"How do I extract the calculateTotal() method?"
```

Copilot will provide concrete examples.

---

### **Tip 3: Follow Up with Refactoring Skill**

After understanding smells:
```
#file:.github/prompts/refactoring.prompt.md

Fix these smells from the quality report:
1. Extract methods from placeOrder()
2. String status → OrderStatus enum
3. Introduce OrderItemDTO

Provide refactored code with changelog.
```

---

### **Tip 4: Focus on Top 3**

Don't try to fix all 12 smells at once:
1. Pick highest impact (testability, duplication)
2. Fix those first
3. Rerun Code Quality skill
4. Fix next 3
5. Repeat until satisfied

---

## 🚨 When to Use Code Quality Skill Most

### **High Priority** 🔴
- **Before major refactoring** (understand what to fix)
- **When code is hard to test** (find the smell)
- **When duplicate code appears** (identify all instances)
- **When onboarding new team member** (teach what's "off")

### **Good to Use** 🟡
- **Code review discussions** (reference specific smells)
- **Planning next sprint** (what to improve)
- **When stuck** (get a fresh perspective)

### **Optional** 🟢
- Simple bug fixes (usually no smells)
- Documentation updates (no code changes)

---

## 📊 Workflow: Quality → Refactoring → PR → Merge

```
LOCAL DEVELOPMENT              IMPROVEMENT PLANNING            PR & MERGE
═══════════════════════════════════════════════════════════════════════════

Write code
  ↓
Tests pass ✅
  ↓
"Code works but feels messy"
  ↓
USE CODE QUALITY SKILL ← ← ← Identify smells
(Understand issues)           Rank by priority
  ↓
Get priorities:
  1. String → Enum
  2. Extract Methods
  3. Introduce DTO
  ↓
USE REFACTORING SKILL ← ← ← Get fixed code
(Apply fixes)                + changelog
  ↓
Run tests ✅
  ↓
Commit improved code
  ↓
git push ───────────────→ Push to GitHub
                          ↓
                          Create PR ────→ PR #123 created
                          ↓
                          GitHub Actions ✅
                          ↓
                          Team reviews ──→ Approves quickly
                          (Better code)
                          ↓
                          Merge ────────→ In master ✅
```

---

## 🎓 Common Smells & Quick Fixes

| Smell | Example | Fix |
|-------|---------|-----|
| Long Method | placeOrder() 38 lines | Extract validate(), addItems(), calc(), notify() |
| String Status | "PENDING", "SHIPPED" | Create OrderStatus enum |
| Duplicate Code | calc logic in 2 places | Extract calculateTotal() method |
| Feature Envy | Service accessing Order.items | Add domain methods: order.addItem() |
| Data Class | Only getters/setters | Add behavior to the class |
| Primitive Obsession | List<Map<String, Object>> | Create CreateOrderItemRequest DTO |
| Optional.get() | findById(id).get() | Use .orElseThrow(() -> new NotFoundException) |
| System.out.println | println in service | Use SLF4J log.info() |
| Public mutable fields | public BigDecimal total | Make private with getter/setter |
| Message Chains | order.items.get(i).getPrice() | Extract orderItem.calculateLineTotal() |

---

## ✅ Summary

**You'll use Code Quality Skill:**

1. **To understand what's wrong** (quick, detailed analysis)
2. **To get priorities** (what to fix and in what order)
3. **To plan refactoring** (roadmap before coding)

**Benefits:**
- ✅ Understand why code feels "off"
- ✅ Fix highest-impact issues first
- ✅ Avoid random, ineffective refactoring
- ✅ Learn better design patterns
- ✅ Make informed decisions

**When Code Feels Messy:**

1. Use Code Quality skill → "Analyze this file"
2. Get detailed report → Read priorities
3. Decide which to fix (top 3 usually)
4. Use Refactoring skill → "Fix these smells"
5. Get fixed code + changelog
6. Run tests ✅
7. Commit improved code
8. Done! 🎉

---

## 🚀 Your Next Steps

1. **When you finish coding:**
   ```
   #file:.github/prompts/code-quality.prompt.md
   
   Analyze for code smells:
   #file:src/main/java/com/demo/service/YourService.java
   ```

2. **Get detailed report**

3. **Review top 3 priorities**

4. **Use Refactoring skill to fix them**

5. **Run tests**

6. **Commit and push** 🎉

