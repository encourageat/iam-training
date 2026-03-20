terraform {
  required_providers {
    auth0 = {
      source  = "auth0/auth0"
      version = ">= 1.0.0" #use latest version
    }
  }
}

provider "auth0" {}

resource "auth0_client" "auto_client" {
  name            = "AutoApp"
  description     = "AutoApp through terraform"
  app_type        = "regular_web"
  callbacks       = ["http://localhost:3000/callbackt"]
  oidc_conformant = true

  jwt_configuration {
    alg = "RS256"
  }
}

