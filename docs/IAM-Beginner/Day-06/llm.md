**# Installing Ollama and Building a Local AI Agent with MCP and Keycloak**



**# 1. Introduction**



**This guide explains how to:**



**\* Install Ollama on Windows**

**\* Run a local LLM**

**\* Build a simple AI Agent backend**

**\* Connect MCP Client and MCP Server**

**\* Secure MCP transport using Keycloak**



**This setup allows learning AI agents and MCP without paying for cloud AI APIs.**



**---**



**# 2. Recommended Architecture**



**```text id="jlwmku"**

**React Chat UI**

&#x20;    **↓**

**Node.js Backend Agent**

&#x20;    **├── Ollama**

&#x20;    **├── MCP Client**

&#x20;    **├── Keycloak Authentication**

&#x20;    **└── Tool Orchestration**

&#x20;           **↓**

&#x20;      **MCP Server**

**```**



**---**



**# 3. What Is Ollama?**



**Ollama is a lightweight runtime for running LLMs locally on your laptop.**



**It allows you to run models such as:**



**\* Llama 3**

**\* Mistral**

**\* Qwen**

**\* Phi-3**

**\* Gemma**



**without needing cloud APIs.**



**---**



**# 4. System Requirements**



**## Minimum Recommended**



**| Component  | Recommendation       |**

**| ---------- | -------------------- |**

**| RAM        | 16 GB                |**

**| CPU        | Intel i5/i7 or Ryzen |**

**| Disk Space | 20–40 GB             |**

**| GPU        | Optional             |**



**---**



**# 5. Download and Install Ollama**



**## Step 1 — Download Ollama**



**Visit:**



**\[Ollama Official Website](https://ollama.com?utm\_source=chatgpt.com)**



**Download the Windows installer.**



**---**



**## Step 2 — Install**



**Run the installer normally.**



**After installation:**



**Open Command Prompt and verify:**



**```bash id="m1iq77"**

**ollama --version**

**```**



**---**



**# 6. Running Your First Local LLM**



**## Option 1 — Lightweight Model**



**Recommended for beginners:**



**```bash id="5sqrd5"**

**ollama run phi3**

**```**



**---**



**## Option 2 — Better General Model**



**```bash id="7hij4w"**

**ollama run llama3**

**```**



**First time execution:**



**\* model downloads**

**\* then starts locally**



**This may take several minutes depending on internet speed.**



**---**



**# 7. Understanding What Happens Internally**



**Ollama exposes a local HTTP API.**



**Example:**



**```text id="1nbj8r"**

**http://localhost:11434**

**```**



**Your backend agent communicates with Ollama through REST APIs.**



**Architecture:**



**```text id="9m91l5"**

**Node.js Backend**

&#x20;       **↓**

**Ollama API**

&#x20;       **↓**

**Local LLM**

**```**



**---**



**# 8. Test Ollama API**



**Open another terminal and test:**



**```bash id="3g5vq9"**

**curl http://localhost:11434/api/tags**

**```**



**You should see installed models.**



**---**



**# 9. Create Node.js Backend**



**## Step 1 — Create Project**



**```bash id="0gh0k6"**

**mkdir ai-agent-demo**

**cd ai-agent-demo**

**npm init -y**

**```**



**---**



**## Step 2 — Install Dependencies**



**```bash id="m76plu"**

**npm install express**

**npm install typescript ts-node @types/node @types/express**

**```**



**Optional:**



**```bash id="1u2v2d"**

**npm install axios**

**```**



**---**



**# 10. Create Simple Ollama Client**



**Create:**



**```text id="m2e4sv"**

**src/ollama.ts**

**```**



**Example:**



**```typescript id="9bg4wl"**

**const response = await fetch("http://localhost:11434/api/chat", {**

&#x20; **method: "POST",**

&#x20; **headers: {**

&#x20;   **"Content-Type": "application/json"**

&#x20; **},**

&#x20; **body: JSON.stringify({**

&#x20;   **model: "llama3",**

&#x20;   **messages: \[**

&#x20;     **{**

&#x20;       **role: "user",**

&#x20;       **content: "Multiply 5 and 8"**

&#x20;     **}**

&#x20;   **]**

&#x20; **})**

**});**

**```**



**---**



**# 11. Understanding AI Agent Flow**



**The LLM does NOT execute tools.**



**Instead:**



**```text id="t2m7hn"**

**LLM → decides tool call**

**Backend → executes tool**

**```**



**Architecture:**



**```text id="3dcgq6"**

**User**

&#x20;**↓**

**Chat UI**

&#x20;**↓**

**Backend Agent**

&#x20;    **├── Ollama**

&#x20;    **├── MCP Client**

&#x20;    **└── Tool Orchestration**

&#x20;**↓**

**LLM decides tool**

&#x20;**↓**

**MCP Client calls MCP Server**

&#x20;**↓**

**Tool Result**

&#x20;**↓**

**LLM formats response**

**```**



**---**



**# 12. Why MCP Client Exists**



**The MCP Client acts as bridge between:**



**```text id="z4p8wi"**

**LLM ↔ MCP Server**

**```**



**The MCP client:**



**\* discovers tools**

**\* invokes tools**

**\* manages protocol**

**\* handles authentication**

**\* manages transport**



**---**



**# 13. MCP Server Example**



**Suppose your MCP server exposes:**



**\* add**

**\* multiply**



**Example:**



**```typescript id="j7m13w"**

**server.tool(**

&#x20; **"multiply",**

&#x20; **async ({a, b}) => a \* b**

**);**

**```**



**---**



**# 14. Tool Calling Flow**



**Suppose user asks:**



**```text id="w2kg8h"**

**Multiply 5 and 8**

**```**



**---**



**## Step 1 — LLM Decides Tool**



**LLM internally returns:**



**```json id="3cm5ks"**

**{**

&#x20; **"tool\_calls": \[**

&#x20;   **{**

&#x20;     **"name": "multiply",**

&#x20;     **"arguments": {**

&#x20;       **"a": 5,**

&#x20;       **"b": 8**

&#x20;     **}**

&#x20;   **}**

&#x20; **]**

**}**

**```**



**---**



**## Step 2 — Backend Executes Tool**



**Backend invokes MCP client:**



**```typescript id="9lhn72"**

**const result = await mcpClient.callTool({**

&#x20;  **name: "multiply",**

&#x20;  **arguments: {**

&#x20;     **a: 5,**

&#x20;     **b: 8**

&#x20;  **}**

**});**

**```**



**---**



**## Step 3 — MCP Server Executes**



**MCP server executes:**



**```text id="pifk7n"**

**multiply(5, 8)**

**```**



**Result:**



**```json id="9yo8cq"**

**{**

&#x20; **"result": 40**

**}**

**```**



**---**



**## Step 4 — Backend Sends Result to LLM**



**LLM then generates:**



**```text id="hv7m6y"**

**5 multiplied by 8 is 40.**

**```**



**---**



**# 15. Adding Keycloak Security**



**Keycloak secures communication between:**



**```text id="phmbgv"**

**MCP Client ↔ MCP Server**

**```**



**---**



**# 16. Keycloak Authentication Flow**



**## Step 1 — MCP Client Gets Token**



**Example:**



**```http id="76g0hu"**

**POST /realms/demo/protocol/openid-connect/token**

**```**



**---**



**## Step 2 — MCP Client Sends Bearer Token**



**```http id="2fxm54"**

**Authorization: Bearer eyJ...**

**```**



**---**



**## Step 3 — MCP Server Validates Token**



**The MCP server validates:**



**\* JWT signature**

**\* scopes**

**\* roles**

**\* permissions**



**---**



**# 17. Suggested Learning Models**



**## Lightweight Models**



**| Model       | Size    |**

**| ----------- | ------- |**

**| Phi-3 Mini  | \~2–4 GB |**

**| Gemma 2B    | \~2 GB   |**

**| Qwen 2.5 3B | \~2–3 GB |**



**---**



**## Better General Models**



**| Model      | Size    |**

**| ---------- | ------- |**

**| Llama 3 8B | \~4–5 GB |**

**| Mistral 7B | \~4–5 GB |**

**| Qwen 7B    | \~4–5 GB |**



**---**



**# 18. Recommended Beginner Stack**



**## Frontend**



**\* React**

**\* Next.js**



**## Backend**



**\* Node.js**

**\* Express**

**\* TypeScript**



**## AI**



**\* Ollama**



**## MCP**



**\* MCP TypeScript SDK**



**## Security**



**\* Keycloak**



**---**



**# 19. Important Final Concept**



**The MOST IMPORTANT thing to remember:**



**```text id="s5frmo"**

**LLM does NOT execute tools.**

**```**



**Instead:**



**```text id="gpc3iz"**

**LLM → decides tool call**

**Backend Agent → executes tool using MCP Client**

**MCP Server → performs actual work**

**```**



**The backend agent orchestrates the entire flow.**



**---**



**# 20. Next Learning Steps**



**Recommended next steps:**



**1. Install Ollama**

**2. Run local model**

**3. Build simple Express backend**

**4. Build MCP server with add/multiply tools**

**5. Add MCP client**

**6. Add Keycloak OAuth2 security**

**7. Build React chat UI**

**8. Add conversation memory**

**9. Experiment with multi-tool orchestration**



**This gives practical understanding of:**



**\* AI agents**

**\* MCP architecture**

**\* LLM tool calling**

**\* OAuth2 security**

**\* Enterprise AI integrations**



