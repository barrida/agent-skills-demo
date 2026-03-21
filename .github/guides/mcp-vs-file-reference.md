## `#file:` Reference vs. MCP Filesystem Server

### How each one actually works

| Dimension | `#file:` attachment | MCP `filesystem` server |
|---|---|---|
| **Who reads the file** | The IDE (JetBrains plugin) reads it at prompt-send time and injects the content into the context window | Claude decides autonomously which files to call `read_file` on, at inference time |
| **When it happens** | Before the request is sent — content is baked into the prompt | During the conversation — Claude issues tool calls mid-response |
| **What Claude sees** | A static text dump of the file(s) you picked | The result of a tool call it chose to make |
| **Who decides what to read** | You do, explicitly | Claude does, based on your instruction |
| **Context window cost** | Always pays the full file token cost, even if Claude only needed one method | Claude can read only what it needs (e.g. one file, then a second if needed) |
| **Can it explore?** | No — you must know and name every file upfront | Yes — Claude can `list_directory`, find related files, follow imports |
| **Can it write back?** | No — `#file:` is read-only injection | Yes — MCP exposes `write_file`, `edit_file`, `create_directory` etc. |
| **Works without IDE?** | No — tightly coupled to the Copilot plugin's file picker | Yes — any MCP-compatible client works the same way |
| **Staleness risk** | None — reads file at send time | Minimal — reads file at tool-call time (milliseconds later) |

---

### The key conceptual difference

```
#file: approach (passive injection)
──────────────────────────────────
You → [IDE reads files] → prompt with file content baked in → Claude responds
                                                                      ↑
                              Claude only reacts to what you gave it

MCP approach (active tool use)
──────────────────────────────
You → prompt (no file content yet) → Claude thinks → calls read_file() →
      gets content → thinks again → calls read_file(another file) → responds
             ↑
     Claude drives its own information gathering
```

---

### Where the difference becomes obvious

**Scenario: "Review OrderService and fix any issues you find"**

| Step | `#file:` | MCP |
|---|---|---|
| 1 | You must manually attach OrderService.java | Claude reads OrderService.java autonomously |
| 2 | Claude sees only what you attached | Claude notices `OrderRepository` is referenced → calls `read_file` on it too |
| 3 | If Claude needs Order.java to understand the entity, it has to ask you | Claude calls `read_file(Order.java)` on its own |
| 4 | Claude responds based on partial context | Claude responds after building its own full picture |
| 5 | Can't apply fixes — you copy-paste suggestions manually | Claude calls `write_file` to apply changes directly |

---

### So why does the prompt *look* the same?

Because for a **single well-known file + a prompt file**, the outcome is nearly identical.
The difference compounds when:
- The task spans **multiple files** you don't know upfront
- You want Claude to **apply changes**, not just describe them
- You want Claude to **explore** the project (find all services, check all tests, etc.)
- You want to use the same workflow **outside the IDE** (CI, scripts, other tools)

---

### Practical rule of thumb

| Use `#file:` when… | Use MCP when… |
|---|---|
| You know exactly which 1–3 files are relevant | The task requires exploring or discovering files |
| You want a quick inline review in chat | You want Claude to read → reason → write back |
| You're doing a one-off question | You're running a repeatable agent workflow |
| MCP isn't set up yet | MCP is configured and you want full agent behaviour |

---

### PRU Efficiency

```
#file: approach
────────────────
1 user message → 1 LLM inference → 1 response
= 1 PRU (flat, regardless of file size)

MCP approach
────────────────
1 user message → LLM inference → tool call: list_directory
              → LLM inference → tool call: read_file(OrderService.java)
              → LLM inference → tool call: read_file(Order.java)
              → LLM inference → tool call: read_file(OrderController.java)
              → LLM inference → final response
= N PRUs  (one per inference/tool-call round trip)
```

| Scenario | `#file:` PRU cost | MCP PRU cost | Winner |
|---|---|---|---|
| Review 1 known file | 1 PRU | 2–3 PRUs | `#file:` |
| Review 5 known files | 1 PRU (all attached) | 6–10 PRUs | `#file:` |
| Review entire project (unknown files) | Many PRUs (multiple chats) or impossible | 10–20 PRUs (one session) | **MCP** |
| Review + apply fixes | Impossible (read-only) | 15–30 PRUs | **MCP** (only option) |

> **`#file:` is PRU-efficient. MCP is *task*-efficient.**
