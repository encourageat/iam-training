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

## Using Access Token

Call protected API using:
```
curl --request GET \
  --url https://api.example.com/payments \
  --header 'Authorization: Bearer ACCESS_TOKEN'
```

---  

## Practical Session

- Execute Client Credentials flow using `curl`  
- Use an Authorization Server (e.g., Auth0)  
- Retrieve access token  
- Decode JWT using https://jwt.io  
- Verify claims (`aud`, `exp`, etc.)  

---

## Framework Support

Most programming languages provide libraries to:

- Validate JWT tokens  
- Verify signatures  
- Check claims  

### Examples

- Java → Spring Security  
- Node.js → jsonwebtoken  

---

## Microservices Example

### Scenario

Order Service calls Payment Service  

### Steps

1. Order Service requests access token  
2. Receives token from Authorization Server  
3. Calls Payment API with:  

Authorization: Bearer <access_token>  


4. Payment Service validates token  
5. Grants access  

---

## Token Expiry and Refresh

- Access tokens are **short-lived**  
- Refresh tokens are used to obtain new tokens  

  **Note:**
- Refresh tokens are **NOT used** in Client Credentials flow  

---

## OIDC Scope

```
scope=openid
```

- Returns **ID Token**  
- ID Token contains user identity information  

---

## What is a Digital Signature?

A digital signature ensures:

- Integrity (data not altered)  
- Authenticity (trusted issuer)  

### How it Works

- Token is signed using private key  
- Resource Server verifies using public key  

---

## Token Introspection vs Signature Validation

### Token Introspection

- Resource Server calls Authorization Server  
- Checks if token is valid  

#### Pros

- Real-time validation  

#### Cons

- Network call required  
- Slower  

---

### Signature Validation (JWT)

- Resource Server validates token locally  

#### Pros

- Fast  
- No network call  

#### Cons

- Cannot revoke instantly (until expiry)  

---

## Summary

- OAuth 2.0 provides authorization framework  
- OIDC adds authentication layer  
- JWT is commonly used for access tokens  
- Client Credentials flow is used for M2M communication  
- Resource Server validates access token  
- Digital signatures ensure trust  
- Token validation can be done via introspection or signature verification  



