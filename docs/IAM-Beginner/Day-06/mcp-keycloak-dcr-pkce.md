# MCP + Keycloak DCR + Authorization Code + PKCE

This guide shows how to use Visual Studio Code as an MCP client while securing the flow with Keycloak dynamic client registration (DCR) and OAuth 2.0 Authorization Code + PKCE.

## 1. What this solves

* `Keycloak` issues tokens
* `DCR` creates a public OAuth client dynamically
* `PKCE` protects the authorization code flow for public clients
* `VS Code` uses the token to call a simple `MCP` backend

This is a good pattern for local developer workflows and proof-of-concepts.

## 2. Keycloak setup

### 2.1 Start Keycloak

Use Docker Compose or plain Docker.

```bash
cat > keycloak-docker-compose.yml <<'EOF'
version: '3.7'
services:
  keycloak:
    image: quay.io/keycloak/keycloak:21.1.1
    command: ['start-dev']
    ports:
      - '8080:8080'
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
EOF

docker compose up -d
```

Open `http://localhost:8080/` and log in with:

* Username: `admin`
* Password: `admin`

Create a realm named `demo`.

### 2.2 Allow dynamic client registration

In the Keycloak admin console, open the `demo` realm and verify that dynamic registration is allowed.

If required, enable the realm setting for dynamic client registration or use an initial access token.

## 3. Get a Keycloak admin token

Use the `admin-cli` client to request an admin token from the `master` realm.

```bash
curl -s -X POST 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=admin-cli' \
  -d 'username=admin' \
  -d 'password=admin' | jq -r '.access_token'
```

Save the returned token as `ADMIN_TOKEN`.

## 4. Register a public client via DCR

Use the Keycloak DCR endpoint to create a `public` client without a secret.

```bash
curl -s -X POST 'http://localhost:8080/realms/demo/clients-registrations/openid-connect' \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "client_name": "vscode-mcp-client",
    "redirect_uris": ["http://localhost:3001/callback"],
    "grant_types": ["authorization_code", "refresh_token"],
    "response_types": ["code"],
    "token_endpoint_auth_method": "none",
    "application_type": "web",
    "scope": "openid profile email offline_access"
  }'
```

The response includes a `client_id`.

Example response:

```json
{
  "client_id": "a1b2c3d4-...",
  "client_secret": "...",
  "client_id_issued_at": 168...,
  "client_secret_expires_at": 0,
  "registration_access_token": "..."
}
```

Because `token_endpoint_auth_method` is `none`, the client is public and relies on PKCE.

## 5. Verify the client

In the Keycloak admin console, open `Clients` and confirm:

* Client type: `public`
* Standard Flow Enabled: `ON`
* Direct Access Grants Enabled: `OFF` (not needed for PKCE auth code)
* Valid Redirect URIs: `http://localhost:3001/callback`

## 6. Use Authorization Code + PKCE

### 6.1 Start the helper server

In this workspace, a sample helper server is included at `mcp-pkce-demo/auth-server.js`.

It does three things:

1. Creates a public Keycloak client automatically via DCR if none exists
2. Builds a PKCE challenge and redirects you to Keycloak login
3. Exchanges the authorization code for tokens

Start it from the sample folder:

```bash
cd "c:\George_Thomas\github-encourageat\iam-training\docs\IAM-Beginner\Day-06\mcp-pkce-demo"
npm install
npm run start:auth
```

The helper server will create the client automatically on first use and save `client-info.json` locally.

Open in your browser:

```text
http://localhost:3001/login
```

Then log in with a user inside the `demo` realm.

### 6.2 Receive tokens

The helper server will exchange the authorization code for an `access_token`.

The response looks like:

```json
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "id_token": "eyJ...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "token_type": "Bearer",
  "scope": "openid profile email offline_access"
}
```

Copy the `access_token` value.

## 7. Call the MCP server from VS Code

A sample MCP server is included at `mcp-pkce-demo/mcp-server.js`.

It validates Keycloak JWTs using the realm JWKS endpoint.

### 7.1 Start the MCP server

```bash
cd "c:\George_Thomas\github-encourageat\iam-training\docs\IAM-Beginner\Day-06\mcp-pkce-demo"
node mcp-server.js
```

### 7.2 Use VS Code REST Client

Install the `REST Client` extension.

Create or open `mcp-pkce-demo/mcp-request.http` and set the `ACCESS_TOKEN` variable.

Send the request. The server should respond with the decoded token claims and the request body.

## 8. Why this is a good pattern

* `DCR` avoids manual client creation for local application onboarding
* `PKCE` makes the auth code flow safe for public clients
* `VS Code` can still act as a client by reusing the token from a helper flow
* `MCP` remains a separate authenticated backend API

## 9. Next steps

For a more polished setup:

* Automate DCR with an initial access token or admin client
* Use a VS Code extension to launch the `/login` flow and capture the token
* Store the token in environment variables or workspace settings
* Add refresh-token support to renew the token automatically
