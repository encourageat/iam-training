# Understanding Keycloak Source Code and Architecture

## Goal

As an IAM consultant or developer, the objective is not to understand every source file in Keycloak. Instead, focus on understanding the major components, request flow, extension points, and runtime architecture.

---

# 1. Keycloak is Not a Traditional Monolithic Application

When Keycloak is started using:

```bash
kc.sh start
```

or

```bash
kc.bat start
```

it launches a single JVM process running on Quarkus.

Internally, this JVM hosts multiple services:

```text
Quarkus Runtime
        |
        +-- Authentication Engine
        +-- Authorization Engine
        +-- User Management
        +-- Session Management
        +-- OIDC Endpoints
        +-- SAML Endpoints
        +-- Admin REST APIs
        +-- Theme Engine
```

Everything runs inside the same process.

---

# 2. Important Source Code Modules

## core

Contains fundamental representations and shared classes.

Examples:

```java
AccessToken
IDToken
RealmRepresentation
UserRepresentation
```

Think of this module as:

```text
Shared IAM Data Structures
```

---

## model

Contains storage abstractions.

Examples:

```java
RealmModel
UserModel
ClientModel
RoleModel
```

Most Keycloak code interacts with these interfaces rather than directly accessing the database.

---

## server-spi

Contains extension points.

Examples:

```java
Authenticator
ProtocolMapper
UserStorageProvider
EventListenerProvider
```

Most custom Keycloak development starts here.

---

## services

Contains the majority of request processing logic.

Examples:

```java
AuthorizationEndpoint
TokenEndpoint
LoginActionsService
```

Responsible for:

* Authentication
* OIDC endpoints
* SAML endpoints
* Admin REST APIs

---

## quarkus

Responsible for startup and runtime bootstrapping.

Contains:

* Configuration
* Runtime initialization
* Build-time processing

Think of it as:

```text
Application Startup Layer
```

---

# 3. Authentication Flow Internals

When a user accesses an OIDC Authorization Endpoint:

```text
/auth
```

the flow is roughly:

```text
AuthorizationEndpoint
        |
        v
AuthenticationProcessor
        |
        v
Authentication Flow
        |
        +-- Cookie Authenticator
        +-- Username Password Form
        +-- OTP Form
        |
        v
User Session Creation
        |
        v
Authorization Code
```

Key classes:

```java
AuthenticationProcessor
DefaultAuthenticationFlow
AuthenticationFlowContext
```

---

# 4. OIDC Token Issuance Flow

When a client exchanges an authorization code:

```text
/token
```

the flow becomes:

```text
TokenEndpoint
        |
        v
Grant Processing
        |
        v
Session Validation
        |
        v
TokenManager
        |
        v
Protocol Mappers
        |
        v
JWT Creation
```

Important class:

```java
TokenManager
```

---

# 5. Database Access Architecture

Keycloak rarely executes SQL directly.

Instead:

```text
Service Layer
        |
        v
UserModel
        |
        v
JPA Provider
        |
        v
Hibernate
        |
        v
PostgreSQL
```

Benefits:

* Database abstraction
* Custom storage providers
* LDAP integration
* Legacy user store integration

---

# 6. Admin Console Architecture

## Important Fact

The Admin Console is a React application.

However, it is NOT hosted on a separate Node.js server.

There is:

```text
NO Node.js Runtime
NO React Dev Server
NO Separate Admin Process
```

The React application is bundled into Keycloak and served as static content.

---

## Runtime Flow

```text
Browser
   |
   +--> Keycloak Server
            |
            +--> React Application
            +--> Admin REST APIs
```

When opening:

```text
/admin/master/console
```

the browser downloads:

* HTML
* JavaScript
* CSS

The React application then calls Admin REST APIs.

---

## Admin Console Source Code

Located under:

```text
js/
 └── apps/
      └── admin-ui/
```

Contains:

```text
components/
pages/
users/
clients/
groups/
roles/
realm-settings/
```

---

# 7. Admin Console Authentication

The Admin Console is itself an OIDC client.

Special client:

```text
security-admin-console
```

Flow:

```text
Admin Console
       |
       +--> Login
       |
       +--> Access Token
       |
       +--> Admin REST API Calls
```

All administrative operations are performed through Admin REST APIs.

---

# 8. Admin Console Request Flow

Example: Open Users Page

```text
React UI
      |
      v
GET /admin/realms/master/users
      |
      v
UsersResource
      |
      v
UserProvider
      |
      v
Hibernate
      |
      v
PostgreSQL
```

The React application never communicates directly with the database.

---

# 9. Recommended Learning Path

## Stage 1 – Core Concepts

Understand:

* Realms
* Users
* Clients
* Roles
* Groups

Classes:

```java
RealmModel
UserModel
ClientModel
```

---

## Stage 2 – Authentication

Study:

```java
AuthenticationProcessor
DefaultAuthenticationFlow
Authenticator
```

---

## Stage 3 – OIDC

Study:

```java
AuthorizationEndpoint
TokenEndpoint
TokenManager
```

---

## Stage 4 – SPIs

Study:

```java
Authenticator
ProtocolMapper
UserStorageProvider
EventListenerProvider
```

These are the most common customization points.

---

## Stage 5 – Session Management

Study:

```java
UserSessionModel
AuthenticatedClientSessionModel
```

Understanding these explains:

* SSO
* Logout
* Refresh Tokens
* Session Expiration

---

# 10. Best Practical Exercise

Run Keycloak from source in IntelliJ IDEA.

Set breakpoints in:

```java
AuthorizationEndpoint
AuthenticationProcessor
TokenManager
UsersResource
```

Perform:

1. Login through Authorization Code Flow.
2. Open Admin Console.
3. View browser Network tab.
4. Trace requests through the debugger.

Follow:

```text
Browser
   |
   v
REST Endpoint
   |
   v
Service Layer
   |
   v
Model Layer
   |
   v
JPA
   |
   v
Database
```

This approach teaches Keycloak architecture much faster than reading the entire codebase.

---

# Key Mental Model

Think of Keycloak as:

```text
Quarkus Runtime
      |
      +-- Authentication Engine
      +-- OIDC Server
      +-- SAML Server
      +-- Session Manager
      +-- User Management
      +-- Admin REST APIs
      +-- React Admin Console
      +-- SPI Framework
```

Once you understand:

```text
AuthorizationEndpoint
      ->
AuthenticationProcessor
      ->
UserSession
      ->
TokenManager
```

you understand the foundation upon which most Keycloak features are built.
