resource "keycloak_realm" "tf_realm" {
  realm   = "acmesub"
  enabled = true
}

resource "keycloak_openid_client" "tf_acme_client" {
  realm_id            = keycloak_realm.tf_realm.id
  client_id           = "acme-app"
  name                = "acme-app"
  enabled             = true

  access_type         = "CONFIDENTIAL"
  standard_flow_enabled = true
  valid_redirect_uris = [
        "http://localhost:8080/openid-callback"
    ]

}
