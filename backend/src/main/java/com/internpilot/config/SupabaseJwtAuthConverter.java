package com.internpilot.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Converts Supabase JWT claims into Spring Security authorities.
 * The user's application role is stored in app_metadata.role within the JWT,
 * or looked up from the profiles table if not present.
 */
public class SupabaseJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Try app_metadata.role first (set via Supabase admin API)
        Map<String, Object> appMetadata = jwt.getClaimAsMap("app_metadata");
        if (appMetadata != null && appMetadata.containsKey("role")) {
            String role = appMetadata.get("role").toString().toUpperCase();
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // Try user_metadata.role (set during sign-up)
        Map<String, Object> userMetadata = jwt.getClaimAsMap("user_metadata");
        if (userMetadata != null && userMetadata.containsKey("role")) {
            String role = userMetadata.get("role").toString().toUpperCase();
            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        }

        // Default to STUDENT if no role claim found
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }
}
