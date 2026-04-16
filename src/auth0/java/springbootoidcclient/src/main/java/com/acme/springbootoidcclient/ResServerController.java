package com.acme.springbootoidcclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResServerController {

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@GetMapping("/greeting")
	public String greeting(Authentication authentication) {

		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			OAuth2AuthorizedClient client = authorizedClientService
					.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

			return "Hello there! " +  authentication.getAuthorities();
		}
		
		return "Hello there!";
	}

	@GetMapping("/greetingwithrole")
	@PreAuthorize("hasAuthority('ROLE_hrrealmperson')")
	public String greetingRole(Authentication authentication) {
		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			OAuth2AuthorizedClient client = authorizedClientService
					.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

			return "Hello there!: " +  authentication.getAuthorities();
		}

		return "Hello there!";
	}

}
