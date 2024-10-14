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
    private final JwtProperties jwtProperties;


    public JwtAuthenticationFilter(JwtUtilsImpl jwtUtilsImpl,
                                   JwtProperties jwtProperties) {
        this.jwtUtilsImpl = jwtUtilsImpl;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.debug("entered to JwtAuthenticationFilter.doFilterInternal");

        // accessToken=...; Path=/; Secure; HttpOnly; Expires=Fri, 12 Sep 2025 02:17:49 GMT;
        String token = getAccessOrRefreshTokenFromCookies(request);
        if (token == null) {
            log.debug("No token found, skipping filter");
            filterChain.doFilter(request, response);
            return;
        }


        if (!jwtUtilsImpl.validate(token)) {
            log.debug("Invalid access token, skipping filter");
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

    /**
     * Extracts the token from the cookies in the request
     *
     * @param request the request containing the cookies
     * @return if the access token is found, it is returned, otherwise the refresh token is returned ( null if not found any of them )
     */
    protected String getAccessOrRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.debug("No cookies found in request");
            return null;
        }

        String accessToken = getAccessTokenFromCookie(cookies);
        String refreshToken = getRefreshTokenFromCookie(cookies);

        return accessToken != null ? accessToken : refreshToken;
    }

    private String getRefreshTokenFromCookie(Cookie[] cookies) {
        String refreshTokenCookieName = jwtProperties.getToken().getRefresh().getCookie().getName();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(refreshTokenCookieName)) {
                log.debug("found refresh token cookie");
                return cookie.getValue();
            }
        }
        return null;
    }

    private String getAccessTokenFromCookie(Cookie[] cookies) {
        String accessTokenCookieName = jwtProperties.getToken().getAccess().getCookie().getName();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessTokenCookieName)) {
                log.debug("found access token cookie");
                return cookie.getValue();
            }
        }
        return null;
    }
}
