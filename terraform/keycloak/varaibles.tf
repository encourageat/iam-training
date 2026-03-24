variable "keycloak_client_id" {
  description = "OIDC client ID for Terraform authentication"
  type        = string
  sensitive   = true
}
variable "keycloak_client_secret" {
  description = "OIDC client secret"
  type        = string
  sensitive   = true
}
variable "keycloak_url" {
  description = "Keycloak base URL"
  type        = string
}
variable "keycloak_realm" {
  description = "Realm used for authentication (typically master)"
  type        = string
}