package com.acme.springbootoidcclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2AuthorizedClientService authorizedClientService) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/greetingwithrole").hasAuthority("ROLE_hrrealmperson")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
            		  .userInfoEndpoint(userInfo -> userInfo
            		    .oidcUserService(new CustomOidcUserService()) // works always for OIDC login
            		  )
            		);
        

        return http.build();
    }
}