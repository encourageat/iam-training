package com.acme.springbootoidcclient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.*;

public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

        Map<String, Object> claims = oidcUser.getClaims();
        System.out.println("ID Token claims: " + claims);
/*
        // Direct 'roles'
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?> roles) {
            for (Object role : roles) {
                mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
*/
        // Or 'resource_access'
        Object resourceAccessObj = claims.get("resource_access");
        if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {
            Object clientAccessObj = resourceAccess.get("springbootclient");
            if (clientAccessObj instanceof Map<?, ?> clientAccess) {
                Object clientRolesObj = clientAccess.get("roles");
                if (clientRolesObj instanceof Collection<?> clientRoles) {
                    for (Object role : clientRoles) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    }
                }
            }
        }

        System.out.println("Mapped authorities: " + mappedAuthorities);

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }
}

