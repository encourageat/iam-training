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
