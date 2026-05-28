# Understanding AI Agents, MCP Client, MCP Server and Keycloak Security

## 1. Introduction

This document explains how:

* AI Agents
* LLMs (ChatGPT/OpenAI)
* MCP Clients
* MCP Servers
* Keycloak

work together in a real-world architecture.

This is especially useful when building secure AI applications using the MCP (Model Context Protocol).

---

# 2. Important Clarification

A common misunderstanding is:

```text
LLM directly talks to MCP Server
```

This is NOT how it works.

The correct architecture is:

```text
LLM ↔ Backend Agent ↔ MCP Server
```

The backend agent acts as the orchestrator.

---

# 3. High-Level Architecture

```text
User
 ↓
Chat UI
 ↓
Backend AI Agent
     ├── LLM Connection
     ├── MCP Client
     ├── Tool Orchestration
     └── Keycloak Token Handling
 ↓
OpenAI API / Local LLM
 ↓
LLM decides tool call
 ↓
MCP Client executes tool
 ↓
MCP Server
 ↓
Tool Result
 ↓
LLM formats final response
 ↓
User
```

---

# 4. Main Components

## 4.1 Chat UI

This is the frontend application.

Examples:

* React
* Next.js
* Angular
* Simple HTML page

Purpose:

* Collect user input
* Display AI responses

Example:

```text
User types:
Multiply 5 and 8
```

---

# 4.2 Backend AI Agent

This is the MOST IMPORTANT layer.

Can be  implemented using:

* Node.js
* Express
* TypeScript
* LangChain
* LangGraph etc.

The backend agent contains:

```text
1. LLM Connection
2. MCP Client
3. Tool Orchestration
4. Conversation Memory
5. Authentication Logic
```

The backend communicates with BOTH:

```text
1. LLM Provider
2. MCP Server
```

---

# 4.3 LLM (ChatGPT/OpenAI)

The LLM is the reasoning engine.

It DOES NOT execute tools directly.

Instead, it decides:

```text
"Which tool should be called?"
```

Example:

User asks:

```text
Multiply 5 and 8
```

LLM responds internally:

```json
{
  "tool_calls": [
    {
      "name": "multiply",
      "arguments": {
        "a": 5,
        "b": 8
      }
    }
  ]
}
```

The LLM only RETURNS the decision.

It does not execute the tool.

---

# 4.4 MCP Client

The MCP Client lives INSIDE the backend agent.

Purpose:

* Discover tools
* Call tools
* Manage MCP protocol
* Handle authentication
* Handle transport

Example:

```typescript
const result = await mcpClient.callTool({
   name: "multiply",
   arguments: {
      a: 5,
      b: 8
   }
});
```

The MCP client is the bridge between:

```text
LLM ↔ MCP Server
```

---

# 4.5 MCP Server

The MCP Server exposes tools.

Example tools:

* add
* multiply

Example:

```typescript
server.tool(
  "multiply",
  async ({a, b}) => a * b
);
```

The MCP server executes business logic and returns results.

---

# 4.6 Keycloak

Keycloak secures communication between:

```text
MCP Client ↔ MCP Server
```

Keycloak provides:

* Authentication
* Authorization
* OAuth2
* OIDC
* JWT tokens
* Role-based access control

---

# 5. End-to-End Flow

## STEP 1 — User Sends Request

User enters:

```text
Multiply 5 and 8
```

---

## STEP 2 — Chat UI Sends to Backend

Example:

```json
{
  "message": "Multiply 5 and 8"
}
```

---

## STEP 3 — Backend Calls LLM

Example:

```typescript
const response = await openai.chat.completions.create({
  model: "gpt-4.1",
  messages: [
    {
      role: "user",
      content: "Multiply 5 and 8"
    }
  ],
  tools: [...]
});
```

---

## STEP 4 — LLM Decides Tool Call

LLM responds:

```json
{
  "tool_calls": [
    {
      "name": "multiply",
      "arguments": {
        "a": 5,
        "b": 8
      }
    }
  ]
}
```

---

## STEP 5 — Backend Executes Tool

Backend sees the tool request.

Backend invokes MCP Client.

```typescript
const result = await mcpClient.callTool({
   name: "multiply",
   arguments: {
      a: 5,
      b: 8
   }
});
```

---

## STEP 6 — MCP Client Authenticates with Keycloak

MCP Client obtains access token.

Example:

```http
Authorization: Bearer eyJ...
```

---

## STEP 7 — MCP Server Validates Token

MCP Server validates:

* JWT signature
* roles
* scopes
* permissions

---

## STEP 8 — MCP Server Executes Tool

```text
multiply(5, 8)
```

Result:

```json
{
  "result": 40
}
```

---

## STEP 9 — Backend Sends Tool Result Back to LLM

Example:

```json
[
  {
    "role": "tool",
    "tool_call_id": "...",
    "content": "40"
  }
]
```

---

## STEP 10 — LLM Generates Final Response

LLM responds naturally:

```text
5 multiplied by 8 is 40.
```

---

# 6. Why MCP Client Is Needed

Without MCP:

```text
LLM → directly coded functions
```

With MCP:

```text
LLM → MCP Client → MCP Server
```

Benefits:

* Remote tools
* Standardized protocol
* Secure access
* Enterprise integrations
* OAuth2 security
* Multi-agent architecture

---

# 7. OpenAI API vs ChatGPT UI

Important distinction:

```text
ChatGPT UI ≠ OpenAI API
```

## ChatGPT UI

Used through browser:

```text
chat.openai.com
```

This is the hosted application.

---

## OpenAI API

Used by developers.

Your backend calls:

```text
OpenAI API
```

using SDKs.

Example:

```typescript
import OpenAI from "openai";
```

The backend communicates with the LLM programmatically.

---

# 8. Can This Be Built Without Paid APIs?

YES.

You can use local LLMs.

---

# 9. Recommended Learning Stack

## Frontend

* React
* Next.js

## Backend

* Node.js
* Express
* TypeScript

## LLM

* Ollama
* Llama 3
* Mistral
* Qwen

## MCP

* MCP TypeScript SDK

## Security

* Keycloak

---

# 10. Suggested Local Architecture

```text
React Chat UI
    ↓
Node.js Backend Agent
    ├── Ollama / Local LLM
    ├── MCP Client
    └── Keycloak Integration
    ↓
MCP Server
```

This avoids OpenAI API costs and is excellent for learning.

---

# 11. Enterprise Relevance

MCP + Keycloak enables:

* Secure AI agents
* OAuth2 protected tools
* Auditable AI access
* Centralized authorization
* Zero-trust AI architecture

This is why IAM knowledge becomes extremely valuable in the AI agent ecosystem.

---

# 12. Key Takeaway

The most important concept:

```text
LLM does NOT execute tools.
```

Instead:

```text
LLM → decides tool call
Backend Agent → executes tool using MCP Client
MCP Server → performs actual work
```

The backend agent is the orchestrator of the entire system.
