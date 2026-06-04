# Keycloak Internal Architecture Notes

## 1. Modern Keycloak Runtime Stack

Keycloak no longer runs on WildFly.

Current architecture:

```text
+------------------------+
|      Keycloak          |
+------------------------+
|       Quarkus          |
+------------------------+
| RESTEasy Reactive      |
| Vert.x                 |
| Hibernate/JPA          |
| Infinispan             |
+------------------------+
|      JVM (JDK)         |
+------------------------+
|         OS             |
+------------------------+
```

### Important

Quarkus is **not** a JRE or JVM.

Keycloak still requires a Java runtime.

```text
Keycloak
   -> Quarkus
      -> JVM
         -> Operating System
```

---

# 2. RESTEasy Reactive

RESTEasy Reactive is Quarkus's implementation of Jakarta REST (JAX-RS).

OIDC endpoints are implemented as JAX-RS resources.

Examples:

```text
/realms/{realm}/protocol/openid-connect/auth

/realms/{realm}/protocol/openid-connect/token

/realms/{realm}/protocol/openid-connect/userinfo
```

Request flow:

```text
HTTP Request
     |
Vert.x
     |
RESTEasy Reactive
     |
OIDC Endpoint
     |
Keycloak Services
```

---

# 3. OIDC vs SAML

## OIDC

Uses:

```text
HTTP
JSON
JWT
REST APIs
```

Flow:

```text
Browser/App
    |
OIDC Endpoint
    |
JWT Tokens
```

## SAML

Uses:

```text
HTTP Redirect Binding
HTTP POST Binding
XML Messages
```

Flow:

```text
Browser
    |
SAML Request (XML)
    |
Keycloak
    |
SAML Response (XML)
```

### SOAP?

Generally no.

Most Keycloak SAML deployments do not use SOAP.

SOAP is only relevant for some advanced SAML specifications and is rarely encountered.

---

# 4. Request Processing Flow

Example:

```http
GET /realms/demo/protocol/openid-connect/auth
```

Internal flow:

```text
Browser
   |
HTTPS Request
   |
Vert.x
   |
RESTEasy Reactive
   |
OIDC Endpoint
   |
KeycloakSession
   |
Providers
   |
Cache (Infinispan)
   |
JPA/Hibernate
   |
Database
```

---

# 5. KeycloakSession

The most important internal object.

Created per request.

### Common misconception

KeycloakSession does NOT store all users, clients, roles, etc.

It is not a cache.

It is more like:

```text
Request Context
+
Provider Registry
+
Transaction Scope
```

---

## What KeycloakSession Provides

```java
session.users()
session.realms()
session.clients()
session.roles()
session.groups()
session.sessions()
```

These methods return provider interfaces.

Example:

```java
UserProvider provider =
      session.users();
```

No database access occurs yet.

---

# 6. Provider Architecture

Example:

```java
session.users()
       .getUserByUsername(...)
```

Flow:

```text
UserProvider
      |
      +--> Cache Layer
      |
      +--> LDAP Provider
      |
      +--> JPA Provider
      |
      +--> Custom Provider
```

The provider hides where the data comes from.

Possible sources:

```text
Database
LDAP
Active Directory
Custom User Storage SPI
```

---

# 7. Infinispan

Infinispan is Keycloak's distributed cache.

Think of it as:

```text
Embedded Redis-like Cache
```

Used heavily to reduce database access.

---

## Cached Objects

```text
Realm Cache
User Cache
Authorization Cache
Session Cache
Keys Cache
Login Failure Cache
```

Typical flow:

```text
Request
   |
Cache Lookup
   |
   +--> Hit -> Return Data
   |
   +--> Miss
          |
          v
      Database
          |
          v
      Cache Updated
```

---

# 8. JPA/Hibernate Layer

If cache misses:

```text
Provider
    |
Hibernate/JPA
    |
JDBC
    |
PostgreSQL/MySQL/etc
```

Entities are loaded from the database.

Examples:

```text
RealmEntity
UserEntity
ClientEntity
```

---

# 9. Authentication Flow

During login:

```text
Browser Flow
     |
Username Password
     |
Conditional OTP
     |
OTP Form
     |
Success
```

Each step invokes an Authenticator.

Example:

```java
public class MyAuthenticator
        implements Authenticator
```

Authentication code accesses Keycloak internals through:

```java
context.getSession()
```

which returns the current KeycloakSession.

---

# 10. AuthenticationSession vs UserSession

## AuthenticationSession

Temporary session during login.

Stores:

```text
OTP Pending
Required Actions
Identity Brokering State
```

Lifecycle:

```text
Login Starts
     |
AuthenticationSession
```

---

## UserSession

Created after successful login.

Stores:

```text
User
Realm
Login Time
IP Address
Authentication Method
```

Lifecycle:

```text
AuthenticationSession
        |
Login Success
        |
UserSession
```

---

# 11. Token Generation

After successful authentication:

```text
TokenManager
     |
Access Token
Refresh Token
ID Token
```

Claims are collected from:

```text
UserModel
RoleModel
GroupModel
Protocol Mappers
```

Result:

```text
Signed JWT Tokens
```

---

# 12. Keycloak Cluster

Example:

```text
Node 1
Node 2
Node 3
```

Infinispan synchronizes important data.

Example logout flow:

```text
Logout on Node 1
       |
Infinispan Event
       |
Node 2 Updated
Node 3 Updated
```

User becomes logged out across the cluster.

---

# 13. Most Important Internal Models

These are the most important interfaces to understand for SPI development:

```java
KeycloakSession

RealmModel
UserModel
ClientModel

UserSessionModel
AuthenticationSessionModel
```

---

# 14. Most Important Providers

```java
UserProvider
RealmProvider
ClientProvider
RoleProvider
GroupProvider
UserSessionProvider
```

Most custom extensions interact with these APIs.

---

# 15. Mental Model

A good way to visualize Keycloak internals:

```text
Request
   |
Vert.x
   |
RESTEasy Reactive
   |
OIDC/SAML Endpoint
   |
KeycloakSession
   |
+--------------------+
| UserProvider       |
| RealmProvider      |
| ClientProvider     |
| SessionProvider    |
| RoleProvider       |
+--------------------+
   |
Infinispan Cache
   |
Hibernate/JPA
   |
Database
```

---

# Key Takeaway

KeycloakSession is NOT a storage container.

Think of it as:

```text
Request Scoped Service Locator
```

It provides access to providers.

Providers then retrieve data from:

```text
Cache
Database
LDAP
Custom Storage
```

This abstraction is what allows the same Keycloak code to work regardless of whether users are stored in:

* Local database
* LDAP
* Active Directory
* Custom User Storage SPI
