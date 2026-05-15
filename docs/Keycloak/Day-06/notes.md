

# Keycloak in Docker

## Install (PostgreSQL, pgAdmin, Keycloak using PG)  

Keycloak uses a new PostgreSQL

compose.yaml

```
version: '3.8'

services:

  postgres:
    image: postgres:16
    container_name: postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: keycloakpassword
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - keycloak-network

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: pgadmin
    restart: unless-stopped
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@encourageat.com
      PGADMIN_DEFAULT_PASSWORD: adminpassword
    ports:
      - "8081:80"
    volumes:
      - pgadmin_data:/var/lib/pgadmin
    depends_on:
      - postgres
    networks:
      - keycloak-network

  keycloak:
    image: quay.io/keycloak/keycloak:26.2.0
    container_name: keycloak
    restart: unless-stopped
    command: start-dev
    environment:
      KC_DB: postgres
      KC_DB_URL_HOST: postgres
      KC_DB_URL_PORT: 5432
      KC_DB_URL_DATABASE: keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloakpassword

      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    networks:
      - keycloak-network

volumes:
  postgres_data:
  pgadmin_data:

networks:
  keycloak-network:
    driver: bridge
```

Run 

```
docker compose up -d
```

After startup:  

Keycloak → http://localhost:8080  
pgAdmin → http://localhost:8081  

pgAdmin Login  

```
Email: admin@encourageat.com
Password: adminpassword
```

Add PostgreSQL Server in pgAdmin  

Inside pgAdmin:  

General Tab  
```
Name: Keycloak PostgreSQL
```

Connection Tab  
```
Host name/address: postgres
Port: 5432
Maintenance database: keycloak
Username: keycloak
Password: keycloakpassword
Verify Keycloak is Using PostgreSQL
```

After startup:  

Login to Keycloak Admin Console  
Create a realm or user  
Open pgAdmin  
Expand:  
Servers  
  -> Keycloak PostgreSQL  
      -> Databases  
          -> keycloak  
              -> Schemas  
                  -> public  
                      -> Tables  

You should see many Keycloak tables like:  

```
realm
user_entity
client
credential
Optional Improvements
```

For production environments, consider:  

Replacing start-dev with optimized production startup  
Using strong passwords  
Enabling HTTPS  
Mounting custom Keycloak themes/providers  
Using .env file for secrets  
Configuring PostgreSQL backups (pg_dump)  
Adding health checks  
Using persistent external volumes  


