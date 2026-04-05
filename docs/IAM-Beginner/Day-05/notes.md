# Day 05 – IAM Beginner Course  
## OIDC Grant Flows, Authentication & Authorization

---

## OIDC Grant Flows (Continued)

---

## Authorization Code Flow

Used in **backend applications** where client secret confidentiality can be maintained.

---

### Flow Diagram

<!-- IMAGE PLACEHOLDER: Authorization Code Flow Diagram -->
![Authorization Code Flow](./images/auth-code-flow.png)

---

### cURL Example – Authorize Endpoint

> Note: `/authorize` is typically invoked via browser redirect. Below is a conceptual example.

```
GET https://YOUR_DOMAIN/authorize?
response_type=code&
client_id=YOUR_CLIENT_ID&
redirect_uri=https://your-app.com/callback&

scope=openid profile email&
state=xyz123
```
---

### cURL Example – Token Endpoint

```bash
curl --request POST \
  --url https://YOUR_DOMAIN/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "grant_type": "authorization_code",
    "client_id": "YOUR_CLIENT_ID",
    "client_secret": "YOUR_CLIENT_SECRET",
    "code": "AUTHORIZATION_CODE",
    "redirect_uri": "https://your-app.com/callback"
}
```

---

## Authorization Code Flow + PKCE

Used in:

- Frontend (SPA) applications
- Mobile applications

👉 Because client secret cannot be securely stored ..

## Flow Diagram
<!-- IMAGE PLACEHOLDER: Authorization Code Flow with PKCE Diagram -->

## cURL Example – Authorize Endpoint (PKCE)  
```
GET https://YOUR_DOMAIN/authorize?
  response_type=code&
  client_id=YOUR_CLIENT_ID&
  redirect_uri=https://your-app.com/callback&
  scope=openid profile email&
  code_challenge=CODE_CHALLENGE&
  code_challenge_method=S256&
  state=xyz123
```

## cURL Example – Token Endpoint (PKCE)  
```
curl --request POST \
  --url https://YOUR_DOMAIN/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "grant_type": "authorization_code",
    "client_id": "YOUR_CLIENT_ID",
    "code": "AUTHORIZATION_CODE",
    "redirect_uri": "https://your-app.com/callback",
    "code_verifier": "CODE_VERIFIER"
}
```
---


