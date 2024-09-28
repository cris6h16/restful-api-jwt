package org.cris6h16.Config.SpringBoot.Security.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

// as a component and not instantiated directly due to this need beans an env properties ( less complexity )
@Component
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

        // accessToken=...; Path=/; Secure; HttpOnly; Expires=Fri, 12 Sep 2025 02:17:49 GMT;
        Cookie[] cookies = request.getCookies();
        String tkCookie = null;

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessTokenCookieName)) {
                tkCookie = cookie.getValue();
            }
        }

        if (jwtUtilsImpl.validate(tkCookie)) {
            Long id = jwtUtilsImpl.getId(tkCookie);
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
