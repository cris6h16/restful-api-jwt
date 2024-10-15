package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtilsImpl jwtUtilsImpl;


    public JwtAuthenticationFilter(JwtUtilsImpl jwtUtilsImpl) {
        this.jwtUtilsImpl = jwtUtilsImpl;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("entered to JwtAuthenticationFilter.doFilterInternal");

        String token = getTokenFromRequest(request);
        if (token == null) {
            log.debug("Token found null, skipping filter");
            filterChain.doFilter(request, response);
            return;
        }


        if (!jwtUtilsImpl.validate(token)) {
            log.debug("Invalid token, skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        Long id = jwtUtilsImpl.getId(token);
        Collection<? extends SimpleGrantedAuthority> authorities = jwtUtilsImpl.getRoles(token).stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList();

        UserDetailsWithId user = new UserDetailsWithId(id, authorities);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("Authenticated user id: {}, authorities: {}", user.getId(), authorities);

        filterChain.doFilter(request, response);
    }


    protected String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer == null) {
            log.debug("Authorization header are null");
            return null;
        }

        return extractToken(bearer);
    }

    private String extractToken(String bearer) {
        log.trace("Extracting token from: '{}'", bearer);
        String token = bearer.substring(7); // *beginIndex* <|> 'Bearer eyJh....'
        log.trace("Extracted token: '{}'", token);
        return token;
    }

}
