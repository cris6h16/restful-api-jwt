package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.CustomUserDetailsService;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Utils.JwtUtilsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtilsImpl jwtUtilsImpl;
    private final CustomUserDetailsService userDetailsService;

    protected final String accessTokenCookieName;

    public JwtAuthenticationFilter(JwtUtilsImpl jwtUtilsImpl,
                                   CustomUserDetailsService userDetailsService,
                                   @Value("${jwt.token.access.cookie.name}")
                                   String accessTokenCookieName) {
        this.jwtUtilsImpl = jwtUtilsImpl;
        this.userDetailsService = userDetailsService;
        this.accessTokenCookieName = accessTokenCookieName;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("entered to JwtAuthenticationFilter.doFilterInternal");

        // accessToken=...; Path=/; Secure; HttpOnly; Expires=Fri, 12 Sep 2025 02:17:49 GMT;
        Cookie[] cookies = request.getCookies();
        String tkCookie = null;

        if (cookies == null) {
            log.debug("No cookies found, then skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessTokenCookieName)) {
                tkCookie = cookie.getValue();
                log.debug("found access token cookie");
                break;
            }
        }

        if (jwtUtilsImpl.validate(tkCookie)) {
            Long id = jwtUtilsImpl.getId(tkCookie);
            UserDetailsWithId user = userDetailsService.loadUserById(id);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("authenticated user: {}", user.getUsername());
        }

        log.debug("leaving JwtAuthenticationFilter.doFilterInternal");
        filterChain.doFilter(request, response);
    }
}
