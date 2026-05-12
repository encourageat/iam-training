

# Install (OpenLDAP, PHP Ldap Admin and Keycloak)

```
docker compose up -d
```

After startup:

Keycloak → http://localhost:8080
phpLDAPadmin → http://localhost:8081

LDAP admin login:

```
Login DN:
cn=admin,dc=encourageat,dc=local

Password:
admin
```

## Create ldap Users

### Create an Organizational Unit

On left tree:

```
dc=encourageat,dc=local
```

Choose:


```
Generic: Organizational Unit
```

Enter:

```
ou: users
```

Create it.

Now your structure becomes:

```
dc=encourageat,dc=local
 └── ou=users

```

### Create User

1. Click your OU

Example:

```
ou=users
```

2. Click

Create a child entry
3. Choose


default -> InetOrgPerson

Fill only simple attributes

Example:
RDN : User Name (uid)
cn: John Doe
sn: Doe
givenName: John
uid: john
mail: john@example.com
userPassword: password


Resulting LDAP DN will become something like:

uid=john,ou=users,dc=encourageat,dc=local

Complete creation

Repeat for other users.

Keycloak Console (within desired realm)- Select User Federation.

Give entries similar to below

```
Vendor: Other

Connection URL:
ldap://openldap:389

Bind DN:
cn=admin,dc=encourageat,dc=local

Bind Credential:
admin

Users DN:
ou=users,dc=encourageat,dc=local

Username LDAP attribute:
uid

RDN LDAP attribute:
uid

UUID LDAP attribute:
entryUUID

User Object Classes:
inetOrgPerson
```

Then:

```
Test connection
Test authentication
```

Save the Configuration

# Install (OpenLDAP, PHP LDAP Admin and Keycloak)  

```bash
docker compose up -d
```

After startup:  

Keycloak → http://localhost:8080  
phpLDAPadmin → http://localhost:8081

LDAP admin login:

```
Login DN:
cn=admin,dc=encourageat,dc=local

Password:
admin
```

## Create LDAP Users

Create an Organizational Unit  

On left tree:  

```
dc=encourageat,dc=local
```

Choose:

```
Generic: Organizational Unit
```

Enter:  

```
ou: users
```

Create it.  

Now your structure becomes:  

```
dc=encourageat,dc=local
 └── ou=users
```

Create User
1. Click your OU

Example:  

ou=users  
2. Click  
Create a child entry  
3. Choose  
Default -> InetOrgPerson  
4. Fill only simple attributes  

Example:  

```
RDN: User Name (uid)  

cn: John Doe
sn: Doe
givenName: John
uid: john
mail: john@example.com
userPassword: password
```

Resulting LDAP DN will become something like:  

```
uid=john,ou=users,dc=encourageat,dc=local
```

Complete creation.  

Repeat for other users.  

Configure LDAP Federation in Keycloak  

Open Keycloak Admin Console within the desired realm.  

Navigate to:  

User Federation  

Add LDAP provider and configure similar to below:  

```
Vendor: Other

Connection URL:
ldap://openldap:389

Bind DN:
cn=admin,dc=encourageat,dc=local

Bind Credential:
admin

Users DN:
ou=users,dc=encourageat,dc=local

Username LDAP attribute:
uid

RDN LDAP attribute:
uid

UUID LDAP attribute:
entryUUID

User Object Classes:
inetOrgPerson
```

Then click:

Test connection
Test authentication

Save the configuration.

Synchronize LDAP Users

After saving:

User Federation
  -> Your LDAP Provider
      -> Synchronize all users

LDAP users should now appear in Keycloak.

Test Login

Example:

Username: john
Password: password

Keycloak should authenticate successfully against LDAP.

