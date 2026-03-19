# Agent Skills Demo — Java / Spring Boot / Gradle

A sample Spring Boot project seeded with intentional code issues to demonstrate
**Claude-powered agent skills** for code review, code quality analysis, and refactoring
directly inside IntelliJ IDEA.

---

## Project Structure

```
agent-skills-demo/
├── .github/
│   ├── copilot-instructions.md          # Global project context for Claude/Copilot
│   └── prompts/
│       ├── code-review.prompt.md        # Agent skill: code review checklist
│       ├── code-quality.prompt.md       # Agent skill: smell detection
│       └── refactoring.prompt.md        # Agent skill: safe refactoring patterns
├── src/
│   └── main/java/com/demo/
│       ├── model/          Order.java, OrderItem.java
│       ├── repository/     OrderRepository.java
│       ├── service/        OrderService.java        ← most issues here
│       └── controller/     OrderController.java
└── build.gradle
```

The source code is **intentionally imperfect** — it has real-world issues that the agent skills
are designed to surface and fix.

---

## Option A — GitHub Copilot + Claude in IntelliJ (Recommended)

### 1. Install the GitHub Copilot Plugin

`Settings → Plugins → Marketplace → search "GitHub Copilot" → Install → Restart`

### 2. Sign in and select Claude as your model

1. `Settings → GitHub Copilot → General`
2. Under **Model**, choose **Claude Sonnet** (or Opus for deeper analysis)
3. Sign in with your GitHub account if prompted

### 3. Enable Copilot Chat and custom instructions

1. Enable the **Copilot Chat** tool window: `View → Tool Windows → GitHub Copilot Chat`
2. The plugin automatically picks up `.github/copilot-instructions.md` — no extra config needed

### 4. Using the agent skills (prompt files)

The `.github/prompts/*.prompt.md` files are **reusable prompt templates**.
IntelliJ Copilot surfaces them via the `#` file reference in the chat:

**In Copilot Chat, type:**
```
#file:.github/prompts/code-review.prompt.md

Review the following file: #file:src/main/java/com/demo/service/OrderService.java
```

Or for refactoring:
```
#file:.github/prompts/refactoring.prompt.md

Refactor: #file:src/main/java/com/demo/service/OrderService.java
```

**Tip:** You can also select code in the editor → right-click → `Copilot → Ask Copilot`,
then reference the skill file in your message.

---

## Option B — Claude.ai (Web) Alongside IntelliJ

If you prefer using Claude.ai directly:

1. Open the skill file (e.g. `code-review.prompt.md`) in your browser or copy its contents
2. Paste the skill instructions + the Java file you want analysed into Claude.ai
3. Claude will apply the checklist and return structured findings

---

## Option C — Anthropic API / MCP Server (Advanced)

For teams wanting Claude embedded in their own tooling:

```bash
# Install the Anthropic MCP server for IntelliJ (JetBrains MCP plugin)
# Settings → Plugins → search "Model Context Protocol"
```

Then in `~/.cursor/mcp.json` or your JetBrains MCP config:
```json
{
  "mcpServers": {
    "claude": {
      "command": "npx",
      "args": ["-y", "@anthropic-ai/mcp-server-claude"],
      "env": { "ANTHROPIC_API_KEY": "your-key-here" }
    }
  }
}
```

---

## Trying the Skills — Quick Start

### Code Review
Open Copilot Chat and run:
```
#file:.github/prompts/code-review.prompt.md

Please review: #file:src/main/java/com/demo/service/OrderService.java
```

Expected findings include: field injection, god method, Optional misuse, magic strings,
System.out.println, public field access, duplicate total calculation logic.

### Code Quality
```
#file:.github/prompts/code-quality.prompt.md

Analyse: #file:src/main/java/com/demo/service/OrderService.java
```

Expected smells: Long Method, Feature Envy, Duplicate Code, Inappropriate Intimacy,
String status flags, Primitive obsession (Map<String,Object> parameter).

### Refactoring
```
#file:.github/prompts/refactoring.prompt.md

Refactor OrderService to address the Long Method and String status smells.
Target file: #file:src/main/java/com/demo/service/OrderService.java
```

Expected output: extracted `validateOrder()`, `buildOrderItems()`, introduced `OrderStatus` enum,
replaced field injection with constructor injection, replaced indexed loops with streams.

---

## Running the Project

```bash
./gradlew bootRun        # start on :8080
./gradlew test           # run unit tests
./gradlew checkstyleMain # run Checkstyle
./gradlew pmdMain        # run PMD
```

---

## Known Issues (intentional — for skill demonstration)

| Class | Issue |
|-------|-------|
| `Order` | Public fields, `java.util.Date`, mutable list returned |
| `OrderItem` | Abbreviated field name `qty`, ambiguous `price` |
| `OrderService` | Field injection, god method, magic strings, System.out.println, duplicate total logic, bare `.get()` on Optional |
| `OrderController` | No DTOs, no `@Valid`, incorrect HTTP status codes, non-RESTful cancel endpoint |
| `OrderRepository` | `findByStatus(String)` instead of typed enum query |
