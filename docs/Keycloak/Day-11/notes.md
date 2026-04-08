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

---

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

---

## Scaling Keycloak Cluster

## 1. Manual Scaling  

- Done using kubectl  

```
kubectl scale deployment keycloak --replicas=3
```

## 2. Automatic Scaling

### a. Horizontal Pod Autoscaler (HPA)

Scales pods based on:
- CPU usage
- Memory usage
- Custom metrics

```bash
kubectl autoscale deployment keycloak --cpu-percent=70 --min=2 --max=10
```

### b. Vertical Pod Autoscaler (VPA)

- Adjusts CPU and memory requests automatically

### c. Cluster Autoscaler

- Adds or removes nodes based on demand

## Updating Keycloak Image

### How updates are performed

Update image tag in:

- Kubernetes Manifest
- OR Helm values.yaml

### Example
```
image:
  tag: 24.0.3
```
## Rolling Updates

Kubernetes Deployment performs **Rolling Update** by default.

### Behavior

- Gradually replaces old pods with new ones
- Ensures zero downtime (if configured properly)

### Configuration

```yaml
strategy:
  type: RollingUpdate
```

## Helm Chart Customization

Helm allows customization using:

### 1. values.yaml

Override default configuration:

```
replicaCount: 3

resources:
  limits:
    cpu: "1"
    memory: "1Gi"
```

### 2. Command Line Overrides

```
helm install keycloak bitnami/keycloak \
  --set replicaCount=3 \
  --set auth.adminUser=admin
```

## Keycloak Operator – Why Use It?

### Primary Benefits

- Automates:
  - Deployment
  - Scaling
  - Updates
  - Backups (depending on implementation)
- Kubernetes-native (uses CRDs)
- Reduces operational complexity
- Better suited for production environments

---

## Managed Kubernetes (EKS, AKS, GKE)

### Example: Amazon EKS

### What AWS Manages

- Control plane (API server, etcd)
- Security patches
- High availability

---

### What YOU Manage

- Worker nodes (unless using Fargate)
- Applications (Keycloak)
- Helm charts / manifests / operators

---

## Updates in Managed Kubernetes

### Cloud Provider Provides

- Kubernetes version upgrades
- Security patches

### But

You must manually upgrade your cluster version (or automate it)

```bash
eksctl upgrade cluster
```

---

## Summary

| Topic | Key Point |
|------|----------|
| Deployment Options | Manifests, Helm, Operator |
| Image Source | ECR, ACR, GCR |
| Scaling | Manual (kubectl), Auto (HPA/VPA) |
| Updates | Rolling updates by default |
| Helm Customization | values.yaml |
| Operator Benefit | Automation & simplicity |
| Managed K8s | Provider manages control plane, not apps |

---

## Trainer Notes

- Start with **Manifests** for fundamentals  
- Move to **Helm** for real-world deployments  
- Introduce **Operator** for advanced automation  

### Demonstrate

- Scaling  
- Rolling updates  
- values.yaml customization  





