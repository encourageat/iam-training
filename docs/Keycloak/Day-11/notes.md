# Keycloak Deployment on Kubernetes

## Deployment Options for Keycloak

There are three primary ways to deploy Keycloak on Kubernetes:

### 1. Manifests (YAML)
- Direct Kubernetes YAML files
- Includes:
  - Deployment
  - Service
  - ConfigMap
  - Secret
- Full control over configuration
- Best for:
  - Learning Kubernetes internals
  - Fine-grained customization

---

### 2. Helm Charts
- Package manager for Kubernetes
- Provides templated Kubernetes manifests
- Uses `values.yaml` for configuration

**Advantages:**
- Reusable and configurable
- Easier upgrades and rollbacks
- Standardized deployment

**Customization:**
- Override default values using:
  - `values.yaml`
  - `--set` command-line options

Example:
```bash
helm install keycloak bitnami/keycloak -f values.yaml