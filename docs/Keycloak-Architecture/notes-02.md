# Keycloak Internal Architecture - Study Notes

## Overview

Modern Keycloak is built on Quarkus and uses several frameworks internally:

```text
+----------------------+
|      Keycloak        |
+----------------------+
|       Quarkus        |
+----------------------+
| RESTEasy Reactive    |
| Vert.x               |
| Hibernate/JPA        |
| Infinispan           |
+----------------------+
| JVM (OpenJDK)        |
+----------------------+
| Operating System     |
+----------------------+
```

---

# Quarkus

## What Quarkus Is

Quarkus is a Java framework optimized for:

* Fast startup
* Low memory usage
* Cloud-native deployments
* Kubernetes environments

Quarkus replaced WildFly as the runtime platform for Keycloak.

## What Quarkus Is Not

Quarkus is NOT:

* A JRE
* A JVM
* An Operating System

Keycloak still runs on a standard JVM.

```text
Keycloak
   |
Quarkus
   |
JVM
   |
OS
```

---

# RESTEasy Reactive

RESTEasy Reactive is Quarkus's implementation of Jakarta REST (JAX-RS).

It handles:

* HTTP request routing
* Parameter extraction
* Response generation
* Endpoint dispatching

Example OIDC endpoints:

```text
/realms/{realm}/protocol/openid-connect/auth

/realms/{realm}/protocol/openid-connect/token

/realms/{realm}/protocol/openid-connect/userinfo
```

Flow:

```text
HTTP Request
      |
Vert.x
      |
RESTEasy Reactive
      |
OIDC Endpoint
      |
Keycloak Logic
```

---

# OIDC vs SAML

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
Application
      |
OIDC Request
      |
Keycloak
      |
JWT Tokens
```

---

## SAML

Uses:

```text
HTTP Redirect Binding
HTTP POST Binding
XML
```

Flow:

```text
Browser
     |
SAML Request XML
     |
Keycloak
     |
SAML Response XML
```

### SOAP

Most Keycloak SAML deployments do NOT use SOAP.

SOAP support is rarely needed and is generally limited to specific advanced SAML scenarios.

---

# Request Processing Flow

Example request:

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
Infinispan Cache
   |
JPA/Hibernate
   |
Database
```

---

# KeycloakSession

## Purpose

A KeycloakSession is created for every incoming request.

It represents:

```text
Request Context
+
Provider Registry
+
Transaction Scope
```

It is NOT:

```text
User Session
Cache
Database Connection Pool
```

---

## Why One Session Per Request?

Imagine:

```text
Request A
Request B
Request C
```

Each request needs:

* Its own transaction
* Its own context
* Thread isolation
* Independent provider access

Therefore:

```text
Request A -> KeycloakSession A

Request B -> KeycloakSession B

Request C -> KeycloakSession C
```

---

## What Does KeycloakSession Contain?

Examples:

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

No user data is loaded yet.

---

# Providers

Providers are Keycloak's abstraction layer.

Example:

```java
session.users()
       .getUserByUsername(...)
```

Possible implementations:

```text
UserProvider
      |
      +-- JPA Provider
      |
      +-- LDAP Provider
      |
      +-- Active Directory
      |
      +-- Custom SPI
```

The caller does not know where the data comes from.

---

# Infinispan

Infinispan is Keycloak's distributed cache.

Think of it as:

```text
Embedded Redis-like Cache
```

---

## Cached Objects

```text
Realm Cache
User Cache
Authorization Cache
Session Cache
Login Failure Cache
Keys Cache
```

Flow:

```text
Request
   |
Cache Lookup
   |
Hit ---> Return Data

Miss --> Database
            |
            v
       Update Cache
```

---

# JPA/Hibernate

If cache misses:

```text
Provider
    |
Hibernate/JPA
    |
JDBC
    |
Database
```

Entities include:

```text
RealmEntity
UserEntity
ClientEntity
```

---

# KeycloakSessionFactory

## Purpose

Singleton object created during Keycloak startup.

Only one instance exists.

```text
Keycloak Startup
        |
KeycloakSessionFactory
```

Think of it as similar to:

```text
Spring ApplicationContext
```

---

## Responsibilities

```text
Register SPIs
Register ProviderFactories
Create KeycloakSessions
Manage Global Resources
```

---

## Request Flow

```text
Incoming Request
        |
KeycloakSessionFactory
        |
Creates
        |
KeycloakSession
        |
Request Processing
        |
Session Closed
```

---

# ProviderFactory

Every provider type has a factory.

Example:

```java
public class MyAuthenticatorFactory
       implements AuthenticatorFactory
```

Loaded once during startup.

---

## Responsibilities

```text
Initialize Provider
Read Configuration
Register Metadata
Create Provider Instances
```

Example:

```java
public Authenticator create(
        KeycloakSession session)
```

The request-scoped KeycloakSession is passed into the provider.

---

# Relationship Between Components

```text
KeycloakSessionFactory
        |
        +----------------+
        |                |
ProviderFactory A   ProviderFactory B
        |
Creates
        |
Provider
        |
Uses
        |
KeycloakSession
```

---

# Model Layer

The model layer is storage-independent.

Keycloak code primarily works with Models, not database entities.

---

# RealmModel

Represents a Realm.

Examples:

```java
realm.getName()

realm.getClients()

realm.getRoles()

realm.isEnabled()
```

Think:

```text
Business Representation of Realm
```

---

# UserModel

Represents a User.

Examples:

```java
user.getUsername()

user.getEmail()

user.getGroups()

user.getRoleMappings()
```

User may originate from:

```text
Local Database
LDAP
Active Directory
Custom SPI
```

The caller does not need to know.

---

# ClientModel

Represents an OAuth/OIDC Client.

Examples:

```java
client.getClientId()

client.getRedirectUris()

client.isPublicClient()
```

Again, storage details are hidden.

---

# Why Models Instead of Entities?

Without Models:

```java
UserEntity
```

would tie everything to a database.

Instead:

```text
UserModel
     |
     +-- JPA User
     |
     +-- LDAP User
     |
     +-- Custom User
```

This abstraction allows Keycloak to support multiple storage systems transparently.

---

# Authentication Flow

Example browser flow:

```text
Username Password
       |
Conditional OTP
       |
OTP Form
       |
Success
```

Each execution invokes an Authenticator.

Example:

```java
public class MyAuthenticator
      implements Authenticator
```

Access to Keycloak internals is through:

```java
context.getSession()
```

which returns the current KeycloakSession.

---

# AuthenticationSession vs UserSession

## AuthenticationSession

Temporary login session.

Stores:

```text
OTP State
Required Actions
Broker Login State
```

Lifecycle:

```text
Login Begins
      |
AuthenticationSession
```

---

## UserSession

Created after successful authentication.

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

# Token Generation

After successful login:

```text
TokenManager
      |
Access Token
Refresh Token
ID Token
```

Claims come from:

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

# Cluster Architecture

Example:

```text
Node1
Node2
Node3
```

Infinispan synchronizes important cache and session information.

Logout flow:

```text
Logout on Node1
       |
Infinispan Event
       |
Node2 Updated
Node3 Updated
```

User is logged out across the cluster.

---

# Most Important Classes To Understand

For SPI development, focus on:

```java
KeycloakSession

KeycloakSessionFactory

ProviderFactory

RealmModel

UserModel

ClientModel

AuthenticationSessionModel

UserSessionModel
```

---

# Mental Model

```text
Request
   |
Vert.x
   |
RESTEasy Reactive
   |
OIDC/SAML Endpoint
   |
KeycloakSessionFactory
   |
KeycloakSession
   |
Providers
   |
Models
   |
Infinispan
   |
JPA/Hibernate
   |
Database
```

---

# Final Summary

```text
KeycloakSessionFactory
    =
    Application-wide Singleton

ProviderFactory
    =
    Creates Providers

KeycloakSession
    =
    Request-scoped Context

Provider
    =
    Access Layer

Model
    =
    Storage-independent Business Object

Entity
    =
    Database Representation
```

This architecture is what allows Keycloak to support:

* Local Users
* LDAP
* Active Directory
* Custom User Storage Providers
* OIDC
* SAML
* Clustering
* Distributed Caching
* Custom SPIs

without changing the business logic that consumes UserModel, RealmModel, ClientModel, and other model interfaces.
