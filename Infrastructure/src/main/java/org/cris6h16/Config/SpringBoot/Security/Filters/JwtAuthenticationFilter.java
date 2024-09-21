package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.CustomUserDetailsService;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.expiration.token.access.cookie.name}")
    private String accessTokenCookieName;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // accessToken=...; Path=/; Secure; HttpOnly; Expires=Fri, 12 Sep 2025 02:17:49 GMT;
        Cookie[] cookies = request.getCookies();
        String tkCookie = null;

        // todo: cookie cannot be null

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessTokenCookieName)) {
                tkCookie = cookie.getValue();
            }
        }

        if (jwtUtils.validate(tkCookie)) {
            Long id = jwtUtils.getId(tkCookie);
            UserDetailsWithId user = userDetailsService.loadUserById(id);

//            Principal principal =
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
