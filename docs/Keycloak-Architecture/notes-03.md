# Keycloak Runtime Architecture Notes



## Tomcat, Quarkus, Request Processing, KeycloakSession and Model Layer



---



# 1. Why Traditional Java Applications Needed Tomcat



Historically Java web applications were packaged as:



```text

myapp.war

```



A WAR file could not accept HTTP requests directly.



It required a container such as:



* Apache Tomcat

* Jetty

* WildFly

* WebLogic

* WebSphere



Architecture:



```text

Browser

  |

HTTP

  |

Tomcat

  |

Servlet Container

  |

WAR Application

```



Tomcat provided:



* HTTP listener

* Request handling

* Thread pool

* Servlet engine

* Session management



The application only implemented business logic.



---



# 2. Why Spring Boot Bundles Tomcat



A Spring Boot application is typically packaged as:



```text

myapp.jar

```



When executed:



```bash

java -jar myapp.jar

```



Tomcat runs inside the application.



Architecture:



```text

+----------------------+

| Spring Application   |

+----------------------+

| Embedded Tomcat      |

+----------------------+

| JVM                  |

+----------------------+

```



Spring Boot still requires:



```text

HTTP Server

Servlet Container

```



Tomcat is simply embedded rather than installed separately.



---



# 3. Why Keycloak Does Not Need Tomcat



Modern Keycloak runs on Quarkus.



Quarkus does not depend on the Servlet API.



Instead it uses:



```text

Vert.x

+

RESTEasy Reactive

```



Architecture:



```text

Browser

&#x20;  |

HTTP

&#x20;  |

Vert.x HTTP Server

&#x20;  |

RESTEasy Reactive

&#x20;  |

Keycloak

```



Because Vert.x itself is an HTTP server:



```text

No Tomcat

No Servlet Container

No WAR Deployment

```



is required.



---



# 4. Traditional Servlet Model vs Keycloak



Traditional Spring MVC:



```text

Browser

&#x20;  |

Tomcat

&#x20;  |

Servlet

&#x20;  |

Spring MVC

&#x20;  |

Business Logic

```



Modern Keycloak:



```text

Browser

&#x20;  |

Vert.x

&#x20;  |

RESTEasy Reactive

&#x20;  |

Keycloak Logic

```



---



# 5. Request Processing Inside Keycloak



Example:



```http

GET /realms/demo/protocol/openid-connect/auth

```



Flow:



```text

Browser

&#x20;  |

HTTPS Request

&#x20;  |

Vert.x

&#x20;  |

RESTEasy Reactive

&#x20;  |

OIDC Endpoint

&#x20;  |

KeycloakSession

&#x20;  |

Providers

&#x20;  |

Infinispan

&#x20;  |

Hibernate/JPA

&#x20;  |

Database

```



---



# 6. Request Threads



For multiple concurrent requests:



```text

Request A

Request B

Request C

```



Conceptually:



```text

Request A

&#x20;   |

Thread A

&#x20;   |

KeycloakSession A



Request B

&#x20;   |

Thread B

&#x20;   |

KeycloakSession B



Request C

&#x20;   |

Thread C

&#x20;   |

KeycloakSession C

```



Each request executes independently.



Important:



Keycloak (through Vert.x and Quarkus) uses:



* Event Loop Threads

* Worker Threads



Internally the implementation is more sophisticated than a simple "one request = one permanent thread".



However, from a Keycloak developer perspective:



```text

Each request receives its own execution context

and its own KeycloakSession.

```



---



# 7. Why KeycloakSession Is Created Per Request



A KeycloakSession represents:



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

Database Cache

Storage Container

```



Lifecycle:



```text

Incoming Request

&#x20;       |

KeycloakSession Created

&#x20;       |

Request Processing

&#x20;       |

Session Closed

```



---



# 8. Why Not Use One Global Session?



Bad design:



```text

Global Session

```



Problem:



```text

Request A

Current User = George



Request B

Current User = John

```



Both requests would overwrite shared state.



Correct design:



```text

Request A

&#x20;   |

KeycloakSession A



Request B

&#x20;   |

KeycloakSession B

```



Complete isolation.



---



# 9. What Does KeycloakSession Actually Contain?



Examples:



```java

session.users();



session.realms();



session.clients();



session.roles();



session.groups();



session.sessions();

```



These methods return provider interfaces.



Example:



```java

UserProvider provider =

&#x20;       session.users();

```



No user data is loaded at this point.



---



# 10. KeycloakSessionFactory



Singleton object created during Keycloak startup.



Only one instance exists.



Lifecycle:



```text

Keycloak Startup

&#x20;      |

KeycloakSessionFactory

```



Think of it as similar to:



```text

Spring ApplicationContext

```



Responsibilities:



```text

Register SPIs



Register Provider Factories



Create KeycloakSessions



Manage Global Resources

```



Request flow:



```text

Request

&#x20;   |

KeycloakSessionFactory

&#x20;   |

Creates

&#x20;   |

KeycloakSession

```



---



# 11. ProviderFactory



Every provider type has a factory.



Example:



```java

public class MyAuthenticatorFactory

&#x20;      implements AuthenticatorFactory

```



Loaded once during startup.



Responsibilities:



```text

Read Configuration



Initialize Provider



Register Metadata



Create Provider Instances

```



Example:



```java

public Authenticator create(

&#x20;     KeycloakSession session)

```



The request-scoped KeycloakSession is supplied to the provider.



---



# 12. Relationship Between Core Components



```text

KeycloakSessionFactory

&#x20;       |

&#x20;       +----------------+

&#x20;       |                |

ProviderFactory A   ProviderFactory B

&#x20;       |

Creates

&#x20;       |

Provider

&#x20;       |

Uses

&#x20;       |

KeycloakSession

```



---



# 13. Providers



Providers are the abstraction layer used to access data.



Example:



```java

session.users()

&#x20;      .getUserByUsername(...)

```



Possible implementations:



```text

UserProvider

     |

     +-- JPA Provider

     |

     +-- LDAP Provider

     |

     +-- Active Directory Provider

     |

     +-- Custom SPI Provider

```



The caller does not know where the data resides.



---



# 14. Model Layer



Keycloak code works primarily with Models.



Not database entities.



---



# RealmModel



Represents a Realm.



Examples:



```java

realm.getName();



realm.getRoles();



realm.getClients();

```



---



# UserModel



Represents a User.



Examples:



```java

user.getUsername();



user.getEmail();



user.getGroups();



user.getRoleMappings();

```



User may come from:



```text

Database

LDAP

Active Directory

Custom User Storage SPI

```



The caller does not need to know.



---



# ClientModel



Represents an OAuth/OIDC client.



Examples:



```java

client.getClientId();



client.getRedirectUris();



client.isPublicClient();

```



Again, storage details are hidden.



---



# 15. Why Models Instead of Entities?



Without models:



```java

UserEntity

```



would tightly couple Keycloak to a database.



Instead:



```text

UserModel

    |

    +-- Database User

    |

    +-- LDAP User

    |

    +-- Active Directory User

    |

    +-- Custom User

```



The rest of Keycloak remains unchanged.



This is one of the key extensibility mechanisms in Keycloak.



---



# 16. Complete Runtime Picture



```text

Browser

  |

HTTPS Request

  |

Vert.x

  |

RESTEasy Reactive

  |

OIDC/SAML Endpoint

  |

KeycloakSessionFactory

  |

Creates

  |

KeycloakSession

  |

Providers

  |

Models

  |

Infinispan Cache

  |

JPA/Hibernate

  |

Database

```



---



# Key Takeaways



```text

Tomcat

   =

   Servlet Container



Spring Boot

   =

   Application + Embedded Tomcat



Vert.x

   =

   HTTP Server used by Quarkus



RESTEasy Reactive

   =

   JAX-RS Implementation



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



Most importantly:



```text

For each incoming request:



1. An execution thread/context is assigned.

2. A new KeycloakSession is created.

3. Providers access data through models.

4. Data may come from cache, database, LDAP, or custom storage.

5. The KeycloakSession is destroyed when the request completes.

```



