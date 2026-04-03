resource "auth0_action" "custom_action" {
  name = "control-access"
  runtime = "node18"
  code = file("${path.module}/actions/control-access.js")

  deploy = true

  supported_triggers {
    id      = "post-login"
    version = "v3"
  }

}

resource "auth0_trigger_actions" "login_flow" {
  trigger = "post-login"

  actions {
    id = auth0_action.custom_action.id
    display_name = auth0_action.custom_action.name
  }
  
}

