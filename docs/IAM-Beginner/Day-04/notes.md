# Day 04 – IAM Beginner Course  
## OAuth 2.0 and OpenID Connect (OIDC)

---

## OAuth 2.0 (Open Authorization)

OAuth 2.0 is an authorization framework that allows applications to obtain limited access to user resources without exposing credentials.

---

## OpenID Connect (OIDC)

OpenID Connect (OIDC) is an **identity layer** built on top of OAuth 2.0.

- OAuth 2.0 → Authorization  
- OIDC → Authentication + Identity  

---

## Common Grant Flows

- Client Credentials Flow  
- Authorization Code Flow  
- Authorization Code + PKCE  
- Resource Owner Password Credentials (ROPC) Flow  

---

## What are Grant Flows?

Grant flows define how a client application obtains an **access token** from the Authorization Server.

They specify:
- How authentication happens  
- How tokens are issued  
- Which actors are involved  

---

## JSON Web Token (JWT)

JWT is a compact, URL-safe token format used in OAuth and OIDC.

### Structure of JWT

```
header.payload.signature
```


### Inspect JWT

You can decode and inspect JWT tokens at:  
 https://jwt.io

---

## JWT Components

### 1. Header
- Algorithm (e.g., RS256)
- Token type

### 2. Payload
- Claims (user data, roles, scopes)
- Example:
  - `sub`
  - `aud`
  - `exp`

### 3. Signature
- Used to verify token integrity
- Generated using private key

---

## Key Concepts in OAuth

---

### Relying Party (RP)

- The application consuming identity
- Similar to **Service Provider (SP)** in SAML

---

### Access Token

- Issued by Authorization Server  
- Used to access protected resources  

**Important:**
- Should be treated as confidential  
- Usually short-lived  
- Often in JWT format  

---

### Resource Server

- Hosts protected APIs / microservices  
- Validates access token before granting access  

### Validation Includes:
- Signature verification  
- Expiry check (`exp`)  
- Audience (`aud`) validation  

---

## Client Credentials Flow

Used for **machine-to-machine (M2M)** communication.

### Example Use Case

- Order Service → calls → Payment Service  

---

### Flow Steps

1. Client (Order Service) requests token from Authorization Server  
2. Authorization Server issues access token  
3. Client calls Resource Server (Payment Service)  
4. Resource Server validates token  
5. Access is granted  

---

## cURL Example – Client Credentials Flow

```bash
curl --request POST \
  --url https://YOUR_DOMAIN/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "client_id": "YOUR_CLIENT_ID",
    "client_secret": "YOUR_CLIENT_SECRET",
    "audience": "YOUR_API_IDENTIFIER",
    "grant_type": "client_credentials"
}
```

## sample response
```
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 86400
}
```

---

