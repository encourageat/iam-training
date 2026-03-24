resource "keycloak_realm" "tf_secrealm" {
  realm   = "acmesec"
  enabled = true
}

resource "keycloak_openid_client" "tf_acmesec_client" {
  realm_id            = keycloak_realm.tf_secrealm.id
  client_id           = "acme-app"
  name                = "acme-app"
  enabled             = true

  access_type         = "CONFIDENTIAL"
  standard_flow_enabled = true
  valid_redirect_uris = [
        "http://localhost:8080/openid-callback"
    ]

}
