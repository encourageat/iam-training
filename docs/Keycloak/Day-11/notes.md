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
```
## 3. Keycloak Operator

Kubernetes-native way to manage Keycloak  
Uses Custom Resource Definitions (CRDs)

### Key Features

- Automates deployment and lifecycle
- Handles scaling, updates, and configuration
- Declarative approach

### Primary Benefit

Simplifies operations by automating complex tasks like:
- Clustering
- Upgrades
- Configuration management

---

## Container Image Registry (ECR, ACR, GCR)

Keycloak images are pulled from container registries:

| Cloud Provider | Registry |
|---------------|---------|
| AWS           | ECR (Elastic Container Registry) |
| Azure         | ACR (Azure Container Registry)   |
| GCP           | GCR / Artifact Registry          |

### Important Note

- Kubernetes itself does **NOT depend on AWS/Azure/GCP**
- It pulls images using:
  - `image:` field in manifests or Helm values

### Example

```yaml
image:
  repository: <account-id>.dkr.ecr.<region>.amazonaws.com/keycloak
  tag: latest
```
