# 🚀 How to Use Pull Request Skill in GitHub Workflow

## Overview: When & Where to Use PR Skill

You'll use the Pull Request skill in **3 key moments**:

```
1. BEFORE pushing    → Self-validate PR locally
2. AFTER pushing     → Quick validation before team review starts
3. DURING review     → Detailed analysis if issues found
```

---

## 📍 Scenario: Your Real GitHub Workflow

Let's walk through a real example with your OrderService refactoring.

### **Step 1: You've Made All Changes Locally** ✅

```
Your branch: feature/refactor-orderservice
Changes made:
  ✅ Extracted methods
  ✅ Created enum
  ✅ Updated tests
  ✅ All tests passing locally
```

### **Step 2: Before Pushing to GitHub**

**OPTION A: Use PR Skill to Self-Validate (RECOMMENDED)** 🎯

Open Copilot Chat in IntelliJ and ask:

```
#file:.github/prompts/pull-request.prompt.md

I'm about to push my branch and create a PR. Please validate:

PR Title: [REFACTOR] Extract methods from OrderService to improve testability

PR Description:
## What Changed
Split OrderService.placeOrder() from 38-line god method into 4 focused methods:
- validatePlaceOrderRequest() - validates inputs
- addItemsToOrder() - processes items
- calculateTotal() - shared calculation (eliminates duplicate)
- notifyCustomer() - sends notifications

## Why
- Reduces cyclomatic complexity from 8 to 2
- Eliminates code duplication
- Improves testability
- Follows DDD principles

## How to Test
1. Checkout branch
2. Run: ./gradlew test (all 3 tests pass)
3. Try creating order: POST /orders with valid items
4. Verify response contains calculated total

## Fixes
Fixes #42

Files Changed:
#file:src/main/java/com/demo/service/OrderService.java
#file:src/test/java/com/demo/OrderServiceTest.java

Tests: All passing (3/3)
Build: Successful

Is this PR ready to push?
```

**Copilot Response:**

```
## Pull Request Review: [REFACTOR] Extract methods from OrderService

### 📝 PR Description & Motivation
✅ Title format correct
✅ Clear what/why/how
✅ Issue linked (#42)
✅ No breaking changes

### 🎯 Scope & Single Responsibility
✅ Single logical change (method extraction)
✅ No scope creep
✅ Reasonable size (~170 LOC)
✅ Focused commits

### ✅ Testing Coverage
✅ Tests updated
✅ All passing (3/3)
✅ Test names follow pattern
✅ AAA pattern used

### 📚 Documentation & Code Quality
✅ Javadoc added to extracted methods
✅ Follows copilot-instructions.md
✅ No dead code
✅ Error messages specific

### 🔒 Security & Performance
✅ No secrets
✅ Input validation present
✅ calculateTotal() efficient (Stream API)

### 🗂️ Dependencies & Build
✅ No new dependencies
✅ ./gradlew clean build passes
✅ Java 17 / Spring Boot 3.2 compatible

### 📋 Follow-Up Items
✅ Tech debt acknowledged (future: event system)
✅ Next steps outlined

---

## ✅ Summary
**Ready to Merge:** YES

**Blockers:** 0
**Warnings:** 0

You're good to push! This PR will review quickly because:
- Clear, focused changes
- Comprehensive tests
- Follows team standards
- Small enough to review in <15 min
```

Then you know: ✅ **Safe to push!**

---

### **Step 3: Push to GitHub & Create PR**

```bash
git push origin feature/refactor-orderservice
```

Go to GitHub → Click "Create Pull Request" button

Fill in the PR form:

```
Title: [REFACTOR] Extract methods from OrderService to improve testability

Description:
## What Changed
Split OrderService.placeOrder() from 38-line god method into 4 focused methods:
- validatePlaceOrderRequest() - validates inputs
- addItemsToOrder() - processes items
- calculateTotal() - shared calculation (eliminates duplicate)
- notifyCustomer() - sends notifications

## Why
- Reduces cyclomatic complexity from 8 to 2
- Eliminates code duplication
- Improves testability
- Follows DDD principles

## How to Test
1. Pull latest code
2. Run: ./gradlew test
3. Try creating order: POST /orders

Fixes #42
```

Click "Create Pull Request"

---

### **Step 4: PR Created on GitHub** 🎉

Now your PR is live at:
```
https://github.com/yourorg/agent-skills-demo/pull/123
```

**What Happens Automatically:**
- GitHub Actions run (if configured): tests, build, checkstyle ✅
- PR appears in team's review queue
- Team members get notified

---

### **Step 5: During Team Review (Optional Use)**

If a **reviewer** (or you doing self-review) wants detailed analysis, they can use the PR skill:

**Reviewer asks Copilot:**

```
#file:.github/prompts/pull-request.prompt.md

GitHub PR #123: [REFACTOR] Extract methods from OrderService

Review the PR changes:
#file:src/main/java/com/demo/service/OrderService.java
#file:src/test/java/com/demo/OrderServiceTest.java

CI/CD Status: ✅ All checks passed
Build: ✅ Success
Tests: ✅ 3/3 passing

Are there any concerns I should flag in code review?
```

---

## 🔄 Real-World Timeline

```
12:00 PM - You finish refactoring locally
           All tests passing ✅

12:15 PM - Use PR skill to validate before pushing
           Copilot says: Ready to merge ✅

12:20 PM - Push to GitHub & create PR
           PR #123 created ✅

12:25 PM - GitHub Actions run automatically
           All checks pass ✅

12:30 PM - Team notified of PR
           Reviewers start reviewing

1:00 PM  - Reviewer uses PR skill for detailed check
           Looks good, approves ✅

1:05 PM  - You merge PR to master
           Changes deployed ✅
```

---

## 📋 You Have 3 Usage Patterns

### **Pattern 1: Pre-Submission Self-Review** (Before pushing)

**When:** Right before `git push`  
**Who:** You (author)  
**What:** Validate PR is ready  
**Command:**
```
#file:.github/prompts/pull-request.prompt.md

I'm about to create a PR. Is it ready?

PR Title: ...
PR Description: ...
Files: #file:...
Tests: All passing

Ready to push?
```

**Benefit:** Catch issues BEFORE your PR is public; faster review time

---

### **Pattern 2: Quick GitHub Check** (After PR created)

**When:** Right after PR created on GitHub  
**Who:** You or reviewer  
**What:** Quick validation of PR completeness  
**Command:**
```
#file:.github/prompts/pull-request.prompt.md

GitHub PR #123: [REFACTOR] Extract methods from OrderService

CI Status: ✅ All checks passed
Tests: ✅ 3/3 passing

Any blockers or warnings?
```

**Benefit:** Quick sanity check; makes sure nothing was missed

---

### **Pattern 3: Detailed Review** (During code review)

**When:** Reviewer wants systematic analysis  
**Who:** Code reviewer  
**What:** Comprehensive PR validation  
**Command:**
```
#file:.github/prompts/pull-request.prompt.md

Code review for GitHub PR #123

Changes:
#file:src/main/java/com/demo/service/OrderService.java
#file:src/test/java/com/demo/OrderServiceTest.java

Review against all checklist items.
Any concerns for approval?
```

**Benefit:** Thorough, systematic check; consistent standards

---

## 🎯 Recommended Workflow for Your Team

### **Step 1: Developer (Before Pushing)**
```
Write code ✅
Run local tests ✅
USE PR SKILL → Validate readiness
Push to GitHub ✅
```

### **Step 2: GitHub (Automatic)**
```
GitHub Actions run ✅
Code coverage check ✅
Build passes ✅
All checks green ✅
```

### **Step 3: Reviewer (Optional)**
```
Pull PR changes locally (optional)
USE PR SKILL → Detailed validation
Review code manually
Approve or request changes
```

### **Step 4: Merge**
```
Squash commits (if needed)
Merge to master
Delete branch
```

---

## 💡 Pro Tips

### **Tip 1: Use Draft PR First**

If unsure, create PR as **Draft**:

```
Instead of: "Create Pull Request"
Click:      "Create draft pull request"

GitHub Status: DRAFT ⚠️

Then:
1. Use PR skill to validate
2. Fix any issues
3. Mark as "Ready for Review"
```

### **Tip 2: Request Specific Reviewers**

After creating PR on GitHub:
```
Click "Reviewers" → Add team members who know this code

For OrderService: Add backend team lead
For OrderController: Add API team
```

### **Tip 3: Link to Issue**

In PR description, link to issue:
```
Fixes #42          → Auto-closes issue when merged
Relates to #45     → Links without closing
Closes #42, #43    → Close multiple issues
```

### **Tip 4: Use PR Template**

Create `.github/pull_request_template.md`:

```markdown
## What Changed
<!-- Describe your changes -->

## Why
<!-- Why is this change needed? -->

## How to Test
<!-- Steps to verify the change -->

## Checklist
- [ ] Tests added/updated
- [ ] Javadoc added
- [ ] No breaking changes
- [ ] Follows code standards

Fixes #[issue number]
```

GitHub auto-fills this for every PR!

---

## 🚨 When to Use PR Skill Most

### **High Priority** 🔴
- **Before pushing** if PR is large (>300 LOC)
- **Before pushing** if it's a refactoring that could break things
- **If tests fail** on GitHub → use skill to find what's missing
- **If reviewer asks for changes** → validate fix before re-pushing

### **Good to Use** 🟡
- **After pushing** as double-check
- **Before merging** to master
- **Reviewer checking** PR before approving

### **Optional** 🟢
- Small fixes or docs changes
- When all GitHub checks already pass

---

## 📊 The Full Picture

```
LOCAL DEVELOPMENT          GITHUB                  REVIEW
═══════════════════════════════════════════════════════════

Write code
  ↓
Run tests locally ✅
  ↓
USE PR SKILL ← ← ← ← → Validate readiness
(Pre-flight check)
  ↓
git push ───────────→ Branch pushed
                       ↓
                       Create PR ───→ PR #123 created
                       ↓
                       GitHub Actions run ✅
                       ↓
                       All checks pass ✅
                                       ↓
                                       Reviewer pulls code
                                       ↓
                                       USE PR SKILL ← Detailed validation
                                       (Optional review)
                                       ↓
                                       Review code manually
                                       ↓
                                       Approve or request changes
                                       ↓
                                       Merge to master ✅
```

---

## 🎓 Quick Comparison: With vs Without PR Skill

### **WITHOUT PR Skill**
```
❌ Push code to GitHub
❌ Wait for GitHub Actions to run (5-10 min)
❌ Hope all checks pass
❌ Reviewer finds issues during manual review
❌ Back-and-forth feedback
❌ Takes 2-3 hours to merge
```

### **WITH PR Skill** ✅
```
✅ Validate with PR skill (2 min) BEFORE pushing
✅ Push only if skill says "ready"
✅ GitHub Actions run (5-10 min) - already confident
✅ All checks pass
✅ Reviewer has fewer issues to comment on
✅ Quick approval & merge (30 min total)
```

**Time Saved:** ~1.5 hours per PR!

---

## 🚀 Your Next Steps

1. **Finish your current refactoring** ✅ (Already done!)

2. **Prepare your PR locally:**
   ```bash
   git checkout -b feature/refactor-orderservice
   # (already have your changes)
   ```

3. **Before pushing, use PR skill:**
   ```
   #file:.github/prompts/pull-request.prompt.md
   
   Validate my PR...
   ```

4. **Get feedback from Copilot**

5. **Push to GitHub:**
   ```bash
   git push origin feature/refactor-orderservice
   ```

6. **Create PR on GitHub website**

7. **Watch GitHub Actions run** ✅

8. **Team reviews PR**

9. **Merge to master** 🎉

---

## 📝 PR Description Template (Ready to Use)

Copy this template for your PR:

```markdown
## Description
Brief summary of changes (1-2 sentences)

## Why
Why is this change needed? (business reason, technical debt, bug fix, etc.)

## How to Test
Steps for reviewers to verify:
1. Checkout this branch: git checkout feature/...
2. Run tests: ./gradlew test
3. Test endpoint: POST /orders with { customerId: "123", items: [...] }
4. Verify response contains calculated total

## Type of Change
- [ ] Bug fix (non-breaking change fixing issue)
- [x] Refactoring (restructuring without behavior change)
- [ ] New feature (non-breaking change adding functionality)
- [ ] Breaking change (fix or feature causing existing functionality change)
- [ ] Documentation update

## Checklist
- [x] My code follows the team's style guidelines
- [x] I have added tests for my changes
- [x] All new/changed tests pass
- [x] I have added Javadoc for public methods
- [x] My changes generate no new warnings
- [x] No new dependencies added
- [x] Changes are backward compatible

## Fixes
Fixes #[issue number]
```

---

## ✅ Summary

**You'll use PR Skill in 3 ways:**

1. **Before Pushing** (Most Important) → Validate readiness locally
2. **After PR Created** → Quick GitHub check
3. **During Review** → Detailed analysis if needed

**Benefits:**
- ✅ Catch issues early (before team sees them)
- ✅ Faster reviews (clear, well-tested PRs)
- ✅ Consistent standards (same checklist every time)
- ✅ Confident merges (nothing missed)

**Next Time You Have Code Ready:**
1. Use PR skill → "Is this ready?"
2. Get feedback → Fix any issues
3. Push to GitHub → PR created
4. Watch team review → Quick approval
5. Merge → Done! 🎉


