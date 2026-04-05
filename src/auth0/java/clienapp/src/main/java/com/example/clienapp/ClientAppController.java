package com.example.clienapp;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientAppController {

	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
		if (oidcUser != null) {
			model.addAttribute("name", oidcUser.getFullName());
			model.addAttribute("email", oidcUser.getEmail());
			model.addAttribute("username", oidcUser.getPreferredUsername());
			model.addAttribute("claims", oidcUser.getClaims());
		}
		return "profile";
	}
	
	@GetMapping("/customep")
	public String receiveAssertion() {
		
		return "redirect:http://localhost:9089";
		
	}
}
