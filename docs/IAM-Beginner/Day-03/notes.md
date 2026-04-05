# Day 03 – IAM Beginner Course  
## Practical + Core Concepts

---

## 1. Concept of Cookies

### What is a Cookie?

A cookie is a small piece of data stored in the user's browser by a website.  
It is used to maintain state between HTTP requests.

---

### Why Cookies are Needed

HTTP is **stateless**, meaning:
- Each request is independent
- Server does not remember previous interactions

Cookies help in:
- Session management
- Authentication
- Personalization

---

### Example

After login:
- Server sends a cookie (e.g., `JSESSIONID`)
- Browser stores it
- Browser sends it with every request

---

### Types of Cookies

#### 1. Session Cookies
- Stored in memory
- Deleted when browser is closed

#### 2. Persistent Cookies
- Stored on disk
- Have expiry time

---

### Important Cookie Attributes

- **HttpOnly** → Not accessible via JavaScript (prevents XSS attacks)
- **Secure** → Sent only over HTTPS
- **SameSite**
  - `Strict`
  - `Lax`
  - `None` (used in SSO scenarios)

---

### Cookies in SSO

- Cookies maintain login session
- Example:
  - Login once via IdP
  - Cookie stored for IdP domain
  - Enables seamless login to multiple apps

---
- Reversible: Yes
- No security

#### Example:
- Base64 encoding

## 2. Encoding vs Encryption vs Hashing

---

### Encoding

- Purpose: Data transformation for transport

```text
Hello → SGVsbG8=

## Encryption

- **Purpose:** Secure data (confidentiality)  
- **Reversible:** Yes (with key)

### Types
- **Symmetric Encryption** (same key)
- **Asymmetric Encryption** (public/private key)

### Examples
- HTTPS communication  
- Token encryption  

---

## Encryption
- **Purpose:** Secure data (confidentiality)
- **Reversible**: Yes (with key)
### Types:
- Symmetric (same key)
- Asymmetric (public/private key)
###Example:
- HTTPS communication
- Token encryption

---


## Hashing

- **Purpose:** Data integrity / password storage  
- **Reversible:** No  

### Properties
- One-way  
- Same input → same output  
- Small change → completely different hash  

### Example
- Password storage using bcrypt  

---

## Quick Comparison

| Feature     | Encoding     | Encryption        | Hashing           |
|-------------|-------------|------------------|-------------------|
| Reversible  | Yes         | Yes              | No                |
| Security    | No          | Yes              | Yes               |
| Use Case    | Data format | Data protection  | Password storage  |

---

## 3. Debugging Techniques

### Browser Inspect (Developer Tools)

Used to analyze:
- Network calls  
- Cookies  
- Headers  
- Redirects  

### Steps

1. Open DevTools (`F12`)  
2. Go to **Network** tab  
3. Filter by:
   - `XHR`
   - `Doc`

### Observe

- `/authorize`  
- `/login`  
- `/callback`  

---

### What to Look For

- Redirect URLs  
- Query parameters (`code`, `state`)  
- Cookies being set  
- Response headers  

---

## SAML Tracer Tool

A browser plugin (Firefox/Chrome) to inspect SAML flows.

### Why Use SAML Tracer

- Capture SAML Requests & Responses  
- Decode Base64 messages  
- View XML assertions  

### What You Will See

- `SAMLRequest`  
- `SAMLResponse`  
- Redirect bindings  
- POST bindings  

---

## 4. Demo: SAML Flow  
(Keycloak as SP and Auth0 as IdP)

---

### Architecture

- **Service Provider (SP):** Keycloak  
- **Identity Provider (IdP):** Auth0  

---

### Flow Steps

1. User accesses application (protected by Keycloak)  
2. Keycloak (SP) redirects user to Auth0 (IdP)  
3. Auth0 authenticates user  
4. Auth0 sends SAML Response back to Keycloak  
5. Keycloak validates assertion  
6. User gains access  

---

### Flow Diagram

<!-- IMAGE PLACEHOLDER: Keycloak SP and Auth0 IdP SAML Flow -->
![SAML Flow Keycloak SP Auth0 IdP](./images/saml-keycloak-auth0-flow.png)

---

## 5. Cookie Behavior in This Flow

---

### Where Cookies Are Created

#### 1. Auth0 Domain
- Stores login session  
- Enables SSO across apps  

#### 2. Keycloak Domain
- Stores session after SAML login  

---

### Important Observation

Cookies are **domain-specific**

**Example:**
- `auth.example.com` → Auth0 cookie  
- `app.example.com` → Keycloak cookie  

---

### SSO Behavior

If user logs in once:

- Auth0 cookie exists  

Next login:
- No login prompt  
- Direct authentication success  

---

## 6. Hands-On Exercise

### Step 1
Open browser DevTools  

### Step 2
Trigger login flow  

### Step 3
Observe:
- Redirects  
- Cookies  
- SAMLRequest  

### Step 4
Use SAML tracer:
- Decode SAMLResponse  
- Inspect assertion  

---

## 7. Summary

- Cookies maintain session state in web applications  
- Encoding, Encryption, and Hashing serve different purposes  
- Debugging tools are essential for IAM understanding  
- SAML flow involves IdP and SP interaction  
- Cookies enable seamless SSO experience  