# Okta Workflows vs Auth0 Actions vs Keycloak SPI vs Inline Hooks vs Protocol Mapper

Modern Identity and Access Management (IAM) platforms provide multiple ways to extend and customize authentication, authorization, and user lifecycle processes.

This guide compares five key extensibility mechanisms:

- Okta Workflows
- Auth0 Actions
- Keycloak SPI (Service Provider Interfaces)
- Okta Inline Hooks
- Keycloak Protocol Mapper

---

```
                 ┌───────────────────────┐
                 │       End User        │
                 └──────────┬────────────┘
                            │
                            ▼
                ┌─────────────────────────┐
                │ Authentication Layer    │
                │-------------------------│
                │ Auth0 Actions           │
                │ Keycloak SPI            │
                │ Okta Inline Hooks       │
                │ Keycloak Protocol Mapper│
                └──────────┬──────────────┘
                           │
                           ▼
                ┌─────────────────────────┐
                │ Token Issuance Layer    │
                └──────────┬──────────────┘
                           │
                           ▼
                ┌─────────────────────────┐
                │ Orchestration Layer     │
                │-------------------------│
                │ Okta Workflows          │
                └──────────┬──────────────┘
                           │
                           ▼
                ┌─────────────────────────┐
                │ External Systems        │
                │-------------------------│
                │ HR / CRM / APIs         │
                │ Risk Engines            │
                │ Notification Systems    │
                └─────────────────────────┘
```

---

## 🔷 Real-Time Authentication Flow (Hooks / Actions / SPI)

```
User Login
    │
    ▼
Identity Provider (Okta / Auth0 / Keycloak)
    │
    ├──► Auth0 Action (JS Logic)
    │
    ├──► Keycloak SPI (Java Logic)
    │
    ├──► Inline Hook ─────► External API
    │                          │
    │                          ▼
    │                   Risk / Business Logic
    │                          │
    ◄──────────────────────────┘
    │
    ▼
Token Issued (Modified / Enriched)
    │
    ▼
Application Access
```
---

# 🧠 1. Okta Workflows

## 🔹 Overview
Okta Workflows is a no-code / low-code automation platform used for identity orchestration across systems.

## 🔹 Execution Model
- Asynchronous
- Runs outside authentication flow

## 🔹 Key Characteristics
- Event-driven automation
- Visual drag-and-drop builder
- Rich connectors (Okta, Slack, APIs, etc.)

## 🔹 Strengths
- No coding required
- Ideal for automation and orchestration
- Integrates multiple systems easily

## 🔹 Limitations
- Cannot modify tokens inline
- Cannot block authentication

## 🔹 Use Cases
- User onboarding / offboarding
- Cross-system provisioning
- Notifications and audit workflows

---

# ⚡ 2. Auth0 Actions

## 🔹 Overview
Auth0 Actions are serverless JavaScript functions that execute during authentication.

## 🔹 Execution Model
- Synchronous
- Runs inside authentication pipeline

## 🔹 Strengths
- Can modify tokens
- Can block login
- Easy to write (Node.js)

## 🔹 Limitations
- Requires coding
- Limited to Auth0 environment

## 🔹 Example
```javascript
exports.onExecutePostLogin = async (event, api) => {
  api.idToken.setCustomClaim("role", "admin");
};
```
## 🔹 Use Cases

- Token enrichment  
- Conditional access logic  
- Custom authentication flows  

---

## 🧩 3. Keycloak SPI (Service Provider Interfaces)

### 🔹 Overview
Keycloak SPI allows deep customization using Java-based extensions.

### 🔹 Execution Model
- Synchronous  
- Runs inside Keycloak server  

### 🔹 Strengths
- Full control over authentication flow  
- Highly extensible  
- Enterprise-grade customization  

### 🔹 Limitations
- Requires Java development  
- Deployment complexity  
- Maintenance overhead  

### 🔹 Use Cases
- Custom authenticators  
- Custom user federation  
- Advanced token generation logic  

---

## 🔗 4. Okta Inline Hooks

### 🔹 Overview
Inline Hooks allow Okta to call an external API during authentication.

### 🔹 Execution Model
- Synchronous (blocking)  
- External service integration  

### 🔹 Strengths
- Real-time decision making  
- Language-agnostic backend  
- Dynamic token enrichment  

### 🔹 Limitations
- Requires external API  
- Latency-sensitive  
- Availability dependency  

### 🔹 Use Cases
- Risk-based authentication  
- Token enrichment via external data  
- Custom policy enforcement  

---

## 🧱 5. Keycloak Protocol Mapper (No-Code / Low-Code)

### 🔹 Overview
Protocol Mappers in Keycloak allow adding or transforming claims in tokens without coding.

### 🔹 Execution Model
- Synchronous  
- Runs during token generation  

### 🔹 Strengths
- No coding required  
- Simple configuration via UI  
- Native integration with tokens  

### 🔹 Limitations
- Limited flexibility  
- Cannot call external APIs  
- No complex logic support  

### 🔹 Use Cases
- Add user attributes to tokens  
- Map roles/groups to claims  
- Simple token customization  

---

## ⚖️ Side-by-Side Comparison

| Feature | Workflows | Auth0 Actions | Keycloak SPI | Inline Hooks | Protocol Mapper |
|--------|----------|--------------|-------------|-------------|----------------|
| Execution | Async | Sync | Sync | Sync | Sync |
| Runs Where | Okta Cloud | Auth0 Runtime | Keycloak Server | External API | Keycloak Server |
| Coding Required | ❌ No | ✅ Yes (JS) | ✅ Yes (Java) | ✅ Yes | ❌ No |
| Token Modification | ❌ | ✅ | ✅ | ✅ | ✅ |
| Blocks Login | ❌ | ✅ | ✅ | ✅ | ❌ |
| External API Call | ✅ | ✅ | ✅ | ✅ | ❌ |
| Complexity | Low | Medium | High | Medium | Low |
| Best For | Automation | Auth Logic | Deep Customization | External Decision | Simple Token Mapping |

---

## 🧠 Architectural Layers

### 🔹 Authentication Layer (Real-time decisions)
- Auth0 Actions  
- Keycloak SPI  
- Okta Inline Hooks  
- Keycloak Protocol Mapper (limited)  

### 🔹 Orchestration Layer
- Okta Workflows  

### 🔹 Integration Layer
- External APIs / Services  

---

## 🚀 When to Use What

### ✅ Use Okta Workflows
- Lifecycle automation  
- Cross-system orchestration  
- Avoid coding  

### ✅ Use Auth0 Actions
- Token customization  
- Quick logic in Auth0 pipeline  

### ✅ Use Keycloak SPI
- Deep customization  
- Full control over authentication  

### ✅ Use Inline Hooks
- External decision making  
- Risk engines / dynamic validation  

### ✅ Use Protocol Mapper
- Simple claim mapping  
- No-code token customization  

---

## 🔥 Real-World Mapping

| Requirement | Recommended Approach |
|------------|--------------------|
| Add simple claim | Protocol Mapper |
| Add dynamic claim | Actions / Hook / SPI |
| Call external risk engine | Inline Hook |
| Automate onboarding | Workflows |
| Replace cron jobs | Workflows |
| Full control over login | Keycloak SPI |

---

## 💡 Final Insight

Modern IAM is not just authentication—it is **event-driven identity orchestration**.

- Workflows → Automation engine  
- Actions / Hooks / SPI → Decision engine  
- Protocol Mapper → Lightweight token customization  

### Choosing the right tool depends on:
- Real-time vs async requirement  
- Complexity of logic  
- Need for external integrations  