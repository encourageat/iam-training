# Keycloak to Microsoft Entra ID Learning Roadmap

## Introduction

For IAM professionals with a strong Keycloak background, learning Microsoft Entra ID (formerly Azure Active Directory) is typically more about understanding Microsoft's terminology, architecture, and ecosystem rather than learning authentication and authorization concepts from scratch.

This document provides a mapping between Keycloak and Entra ID concepts, along with a suggested learning path.

---

# Keycloak to Entra ID Mental Model

| Keycloak            | Microsoft Entra ID                           |
| ------------------- | -------------------------------------------- |
| Realm               | Tenant                                       |
| Client              | App Registration                             |
| Client Scope        | API Scope                                    |
| Protocol Mapper     | Optional Claims / Claims Mapping Policy      |
| Realm Role          | App Role                                     |
| Client Role         | App Role                                     |
| Groups              | Groups                                       |
| Group Mapper        | Group Claims                                 |
| Identity Brokering  | External Identity Provider Federation        |
| User Federation     | Entra Connect / External Identity Sources    |
| Authentication Flow | Authentication Policies & Conditional Access |
| Admin Console       | Entra Admin Center                           |

---

# Understanding Protocol Mappers in Entra ID

One of the first questions a Keycloak administrator typically asks is:

> "What is the equivalent of Protocol Mappers in Entra ID?"

There is no single feature that exactly matches Keycloak Protocol Mappers.

Instead, Entra ID provides multiple mechanisms.

## 1. Optional Claims

Used to include additional standard attributes in tokens.

Examples:

* email
* upn
* groups
* auth_time
* employeeId

Configuration Path:

```text
App Registration
  -> Token Configuration
      -> Add Optional Claim
```

Typical Use Cases:

* Add email to ID Token
* Add group membership to Access Token
* Add employee ID to JWT

---

## 2. Claims Mapping Policies

Used for advanced claim customization.

Examples:

* Rename claims
* Transform claims
* Control claim issuance
* Create custom mappings

Conceptually, this is the closest equivalent to a Keycloak Protocol Mapper.

Example:

```text
employeeId  -> emp_id
```

or

```text
givenName + surname -> full_name
```

---

## 3. Group Claims

Similar to Group Membership Mappers in Keycloak.

Can emit:

* Group IDs
* Group Names
* Security Groups
* Directory Roles

Example Token:

```json
{
  "groups": [
    "Finance",
    "Managers"
  ]
}
```

---

## 4. App Roles

App Roles are commonly used in place of Keycloak Client Roles.

Example:

```text
Inventory.Read
Inventory.Write
Inventory.Admin
```

Token:

```json
{
  "roles": [
    "Inventory.Admin"
  ]
}
```

---

# How Entra ID Handles Authentication for Multiple Applications

A common misconception is that users authenticate separately for each application.

This is not the case.

---

## Keycloak Model

```text
Realm
 ├── Client A
 ├── Client B
 └── Client C
```

Users authenticate once to the realm.

All clients benefit from the SSO session.

---

## Entra ID Model

```text
Tenant
 ├── App Registration A
 ├── App Registration B
 └── App Registration C
```

Users authenticate once to Entra ID.

All applications can leverage the existing SSO session.

---

## Example

### User Accesses App A

```text
Browser
   |
   +--> App A
          |
          +--> Entra Login
```

User authenticates.

Entra creates an SSO session.

---

### User Accesses App B

```text
Browser
   |
   +--> App B
          |
          +--> Entra
```

Entra detects the existing session and issues tokens without requiring another login.

This behavior is very similar to Keycloak.

---

# Why Tokens Differ Between Applications

Although the user authenticates only once, tokens can be different for each application.

Example:

## App A Token

```json
{
  "name": "George",
  "department": "Finance"
}
```

## App B Token

```json
{
  "name": "George",
  "groups": ["Admin", "User"]
}
```

The differences are driven by:

* Optional Claims
* App Roles
* Group Claims
* Claims Mapping Policies
* API Permissions

This is conceptually similar to having different Protocol Mappers configured per Keycloak client.

---

# Client Scopes vs API Scopes

## Keycloak

```text
Realm
   |
   +--> Client Scope
           |
           +--> Protocol Mappers
```

---

## Entra ID

```text
App Registration
    |
    +--> Expose an API
            |
            +--> Scopes
```

Examples:

```text
api://inventory/read
api://inventory/write
api://inventory/admin
```

Applications request these scopes during OAuth/OIDC authorization flows.

---

# Recommended Learning Path for Keycloak Professionals

If you already understand:

* OAuth 2.0
* OpenID Connect
* SAML
* JWT
* Federation

then focus on the Entra-specific capabilities.

## Phase 1 – Core Concepts

### 1. App Registrations

Learn:

* Client IDs
* Redirect URIs
* Certificates
* Client Secrets
* API Permissions

---

### 2. Enterprise Applications

Learn:

* Service Principals
* Application Assignments
* SSO Integrations
* User Provisioning

---

### 3. App Roles

Learn:

* Role Creation
* Role Assignment
* Role Claims

---

### 4. Optional Claims

Learn:

* Token Configuration
* Claim Issuance
* Group Claims

---

### 5. Claims Mapping Policies

Learn:

* Advanced Claim Customization
* Token Transformation

---

## Phase 2 – Identity Management

### 6. Groups and Group Claims

Learn:

* Security Groups
* Microsoft 365 Groups
* Dynamic Groups

---

### 7. Custom Security Attributes

Learn:

* Custom User Metadata
* Authorization Scenarios

---

### 8. Microsoft Graph API

Learn:

* User Management
* Group Management
* Application Management
* Automation

---

## Phase 3 – Enterprise Features

### 9. Conditional Access

One of the most important Entra features.

Learn:

* MFA Enforcement
* Device Compliance
* Location-Based Policies
* Risk-Based Policies

---

### 10. Identity Governance

Learn:

* Privileged Identity Management (PIM)
* Access Reviews
* Entitlement Management
* Lifecycle Workflows

---

# Suggested Hands-On Labs

1. Create an App Registration.
2. Configure OIDC Login.
3. Add Optional Claims.
4. Create App Roles.
5. Assign Users to Roles.
6. Configure Group Claims.
7. Expose an API and Create Scopes.
8. Secure a Spring Boot API with Entra ID.
9. Enable Conditional Access Policies.
10. Query Users and Groups using Microsoft Graph.

---

# Final Thoughts

For an experienced Keycloak administrator or IAM consultant, learning Entra ID is primarily about understanding Microsoft's implementation and ecosystem.

The OAuth 2.0, OpenID Connect, JWT, and SAML concepts remain largely the same.

The biggest areas that typically require dedicated study are:

* Conditional Access
* Identity Governance
* Microsoft Graph
* Enterprise Applications
* Service Principals

Once these concepts are understood, transitioning from Keycloak to Entra ID becomes significantly easier.
