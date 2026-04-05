package com.example.clienapp;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ClientRegistrationRepository clientRegistrationRepository) throws Exception {

        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        "/oauth2/authorization"
                );

        OAuth2AuthorizationRequestResolver customResolver = new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {																																														
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request);
                return customizeRequest(originalRequest, request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest originalRequest = defaultResolver.resolve(request, clientRegistrationId);
                return customizeRequest(originalRequest, request);
            }

            private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest originalRequest, HttpServletRequest request) {
                if (originalRequest == null) {
                    return null;
                }

               // Customize the request parameters
               Map<String, Object> additionalParams = new HashMap<>(originalRequest.getAdditionalParameters());
               
               additionalParams.put("organization", "org_04HAjowDnz8wyQus");
               additionalParams.put("connection", "keycloak-login");


                return OAuth2AuthorizationRequest.from(originalRequest)
                        .additionalParameters(additionalParams)
                        .build();	
            }
        };

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login**", "/error","/customep").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authz -> authz
                    .authorizationRequestResolver(customResolver)
                )
                .defaultSuccessUrl("/profile", true)
            );

        return http.build();
    }
}
