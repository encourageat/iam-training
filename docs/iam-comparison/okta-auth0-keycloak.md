# Okta vs Auth0 vs Keycloak: Customizing Login Flow and Tokens

When working with Identity and Access Management platforms, a key requirement is:

> Control authentication flow behavior and customize tokens dynamically.

While Auth0, Okta, and Keycloak all support this, their approaches differ significantly.

---

## 🔄 Conceptual Mapping

| Capability | Auth0 | Okta | Keycloak |
|-----------|------|------|---------|
| Post-login logic | Actions | Inline Hooks + Policies | Authentication Flows |
| Token customization | Actions (JS) | Token Inline Hook | Protocol Mappers |
| Event triggers | Actions | Event Hooks | Event Listener SPI |
| Custom logic execution | Inside platform | External API | Config / Java SPI |

---

## 🪝 Okta Approach to Customization

In Okta, customization is achieved using a combination of:

- **Inline Hooks (synchronous)**
- **Event Hooks (asynchronous)**
- **Policies (no-code control)**
- **Workflows (low-code automation)**

---

## 🧠 1. Inline Hooks (Closest to Auth0 Actions)

### What are Inline Hooks?

> Inline Hooks allow Okta to call an external API during an authentication or token transaction and wait for a response.

✔️ Synchronous  
✔️ Real-time decision making  
✔️ Can modify tokens and behavior  

---

### 🔐 Token Inline Hook

Closest equivalent to **Auth0 token customization**

#### Flow:

1. User authenticates  
2. Okta prepares token  
3. Calls external API  
4. API responds with additional claims  
5. Token is issued  

#### Example Response

```json
{
  "commands": [
    {
      "type": "com.okta.identity.patch",
      "value": [
        {
          "op": "add",
          "path": "/claims/custom_role",
          "value": "premium"
        }
      ]
    }
  ]
}