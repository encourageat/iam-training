# Microsoft Entra ID, B2B, B2C (External ID) - Notes

## 1. Terminology Changes

Microsoft has renamed several Azure AD products.

| Old Name                          | New Name                                                |
| --------------------------------- | ------------------------------------------------------- |
| Azure Active Directory (Azure AD) | Microsoft Entra ID                                      |
| Azure AD B2B                      | Microsoft Entra External Identities (B2B Collaboration) |
| Azure AD B2C                      | Microsoft Entra External ID for customers               |

---

# 2. Microsoft Entra Product Family

```text
Microsoft Entra
│
├── Entra ID
│     ├── Workforce Identities
│     ├── Enterprise Applications
│     ├── Conditional Access
│     └── External Identities
│            ├── B2B Collaboration
│            ├── B2B Direct Connect
│            └── Cross-Tenant Access
│
├── Entra External ID
│      └── Customer Identity (CIAM)
│
├── Entra Verified ID
│
└── Entra Permissions Management
```

---

# 3. Microsoft Entra ID

Microsoft Entra ID is Microsoft's cloud Identity Provider for workforce identity.

It authenticates:

* Employees
* Contractors
* Internal users
* Enterprise Applications
* Microsoft 365
* Azure Portal
* SaaS applications

Comparable products:

* Okta Workforce Identity Cloud
* Ping Identity Workforce

---

# 4. B2B Collaboration

B2B stands for **Business-to-Business**.

Purpose:

Allow users from another organization to access your applications without creating local passwords.

Typical users:

* Vendors
* Partners
* Consultants
* Suppliers

Example:

```text
Company A
    |
Microsoft Entra ID
    |
Guest User
    |
Partner Company
    |
Keycloak
```

The partner authenticates using Keycloak.

Entra trusts Keycloak and grants access to the application.

---

# 5. External Identities

Inside Microsoft Entra ID there is an administration area called:

```
External Identities
```

This is where administrators configure:

* B2B Collaboration
* External Identity Providers
* Cross-Tenant Access
* External Collaboration Policies

Therefore:

**External Identities is not a separate downloadable product.**

It is a feature area within Microsoft Entra ID.

---

# 6. Can Keycloak be an Identity Provider?

Yes.

Keycloak can act as an external Identity Provider using:

* OpenID Connect
* SAML

Example:

```text
User
  |
Application
  |
Microsoft Entra ID
  |
Redirect
  |
Keycloak
  |
Authenticate User
  |
OIDC/SAML Response
  |
Microsoft Entra ID
  |
Issue Token
  |
Application
```

Entra trusts the assertion received from Keycloak.

---

# 7. Identity Provider Selection

## Keycloak

Keycloak supports:

```
kc_idp_hint
```

Example:

```
...?kc_idp_hint=google
```

The login page is skipped and the user is redirected immediately.

---

## Microsoft Entra

Microsoft uses:

* Home Realm Discovery (HRD)
* domain_hint

Example:

```
...?domain_hint=contoso.com
```

or

```
domain_hint=google
```

This allows Entra to redirect directly to the correct Identity Provider without displaying the provider selection page.

Conceptually:

```
kc_idp_hint
```

≈

```
domain_hint
```

---

# 8. Identity Provider Selection Screen

If multiple providers are configured, Entra can display:

```text
Sign In

○ Microsoft Account

○ Google

○ Facebook

○ Sign in with Keycloak

○ Partner Company
```

The administrator configures the display name when adding the external Identity Provider.

---

# 9. Microsoft Entra External ID for Customers

This is Microsoft's Customer Identity and Access Management (CIAM) platform.

Purpose:

Authenticate:

* Customers
* Consumers
* Mobile App Users
* Website Users
* Citizens

Example:

A bank with five million customers.

Those users are NOT employees.

---

# 10. Is Entra External ID a Separate Product?

Yes.

It is a Microsoft-hosted SaaS product.

You do NOT install it yourself.

Instead, Microsoft hosts it in Azure.

You create an:

```
External Tenant
```

which acts as your CIAM environment.

---

# 11. Configuration in External ID

Typical configuration includes:

* Applications
* Sign-up flows
* Sign-in flows
* Social Login
* MFA
* Branding
* Identity Providers
* Custom OIDC Providers
* Custom SAML Providers

---

# 12. Comparison with Auth0

| Workforce IAM      | Customer IAM (CIAM)         |
| ------------------ | --------------------------- |
| Microsoft Entra ID | Microsoft Entra External ID |
| Okta Workforce     | Auth0                       |

Auth0 and Entra External ID solve similar problems.

Both provide:

* Customer Login
* Social Login
* MFA
* OIDC
* OAuth2
* SAML
* SDKs
* Customer Identity Management

---

# 13. Comparison with Keycloak

| Keycloak            | Microsoft Entra                |
| ------------------- | ------------------------------ |
| Realm               | External Tenant (CIAM)         |
| Identity Providers  | Identity Providers             |
| Identity Brokering  | B2B Collaboration / Federation |
| Authentication Flow | User Flow                      |
| Login Theme         | Branding                       |
| kc_idp_hint         | domain_hint                    |
| Self Hosted         | Microsoft Hosted               |

---

# 14. Self Hosted vs SaaS

## Keycloak

* Download
* Install
* Docker
* Kubernetes
* VM
* Manage Database
* Upgrade Yourself

## Microsoft Entra

* SaaS
* Microsoft Hosts Everything
* No Installation
* Subscription Based
* Managed Updates

---

# 15. Conceptual Mapping

```text
                Workforce Identity

        Microsoft Entra ID
               │
        Enterprise Apps
               │
        External Identities
               │
        B2B Collaboration
               │
        Trust External IdP
               │
            Keycloak
               │
Authenticate User
               │
Entra Issues Token
               │
Application
```

---

# 16. Product Mapping

| Category            | Microsoft           | Okta            | Open Source        |
| ------------------- | ------------------- | --------------- | ------------------ |
| Workforce IAM       | Entra ID            | Okta Workforce  | Keycloak           |
| Customer IAM (CIAM) | Entra External ID   | Auth0           | Keycloak           |
| Identity Federation | External Identities | Okta Federation | Identity Brokering |

---

# 17. Important Points

* Entra ID is primarily a Workforce IAM platform.
* External Identities is an administration area inside Entra ID.
* B2B Collaboration is a capability of External Identities.
* Keycloak can act as an external Identity Provider using OIDC or SAML.
* Microsoft uses `domain_hint` and Home Realm Discovery to select an Identity Provider.
* Entra External ID is Microsoft's Customer Identity (CIAM) platform.
* Entra External ID is a Microsoft-hosted SaaS service, not downloadable software.
* Auth0 is Microsoft's closest competitor in the CIAM space.
* From a Keycloak perspective, Entra External ID feels conceptually similar to running a Keycloak realm dedicated to customer identities, but Microsoft manages the infrastructure and lifecycle.
