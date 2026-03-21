# How to Use MCP with GitHub Copilot in IntelliJ (Option C)

## What is MCP?

Model Context Protocol (MCP) is an open standard that lets AI models like Claude use
**external tools** — such as reading files, calling APIs, or running commands — as part
of a conversation. Rather than you pasting code into the chat, Claude fetches it directly.

```
You: "Review OrderService.java"
Claude (via MCP): list_directory → read_file(OrderService.java) → responds with analysis
```

---

## Prerequisites

| Requirement | How to check |
|-------------|-------------|
| GitHub Copilot plugin (JetBrains) | Settings → Plugins → Installed |
| Node.js + npx | `npx --version` in terminal |
| Claude model selected in Copilot | Settings → GitHub Copilot → Model |

---

## Step 1 — Confirm the config file location

The JetBrains Copilot plugin creates its MCP config at:

```
~/.config/github-copilot/intellij/mcp.json
```

> ⚠️ Common mistake: The README for Cursor uses `~/.cursor/mcp.json` and the key
> `"mcpServers"`. JetBrains Copilot uses a **different path** and the key `"servers"`.

---

## Step 2 — Add the filesystem MCP server

The `@modelcontextprotocol/server-filesystem` package exposes your local file system
as MCP tools (`read_file`, `write_file`, `list_directory`, `search_files`, etc.).

Your config at `~/.config/github-copilot/intellij/mcp.json` should look like this:

```json
{
  "servers": {
    "filesystem": {
      "type": "stdio",
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "/Users/User/workspace/agent-skills-demo"
      ]
    }
  }
}
```

**Key points:**
- `"type": "stdio"` — Claude spawns the server as a child process
- `"command": "npx"` — no pre-install needed; npx downloads on first use
- The last arg is the **root directory** Claude is allowed to access
- You can add multiple root directories as additional args

---

## Step 3 — Restart IntelliJ

MCP servers are loaded at Copilot plugin startup. After editing the config:

1. **File → Invalidate Caches → Just Restart** (or fully quit and reopen IntelliJ)
2. Open the **Copilot Chat** tool window
3. Look for a 🔧 **Tools** button or indicator near the chat input — this confirms
   the MCP server connected successfully

---

## Step 4 — Try it out

### Example 1 — File-aware code review (no copy-paste)
```
Using the filesystem tool, read src/main/java/com/demo/service/OrderService.java
and apply the code-review checklist from .github/prompts/code-review.prompt.md
```
Claude will call `read_file` for both files automatically.

### Example 2 — Project exploration
```
List all Java source files in this project and identify which ones
still have issues mentioned in the README's Known Issues table.
```

### Example 3 — Targeted refactoring
```
Read src/main/java/com/demo/model/Order.java and
src/main/java/com/demo/repository/OrderRepository.java,
then fix the two remaining issues: mutable getItems() and findByStatus(String).
Apply changes following our coding conventions.
```

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| No 🔧 Tools button visible | Restart IntelliJ after editing mcp.json |
| `npx: command not found` | Add Node.js to PATH; check `which npx` in terminal |
| `ENOENT` error in Copilot logs | Verify the directory path in args exists |
| Server listed but tools fail | Check that the path in `args` is readable |

---

## Adding More MCP Servers

You can add multiple servers in the same config:

```json
{
  "servers": {
    "filesystem": {
      "type": "stdio",
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem",
               "/Users/User/workspace/agent-skills-demo"]
    },
    "github": {
      "url": "https://api.githubcopilot.com/mcp/",
      "requestInit": {
        "headers": {
          "Authorization": "Bearer YOUR_GITHUB_TOKEN"
        }
      }
    }
  }
}
```

Reference: https://docs.github.com/en/copilot/customizing-copilot/extending-copilot-chat-with-mcp?tool=jetbrains

