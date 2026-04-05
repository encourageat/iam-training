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

## 2. Encoding vs Encryption vs Hashing

---

### Encoding

- Purpose: Data transformation for transport
- Reversible: Yes
- No security

#### Example:
- Base64 encoding

```text
Hello → SGVsbG8=