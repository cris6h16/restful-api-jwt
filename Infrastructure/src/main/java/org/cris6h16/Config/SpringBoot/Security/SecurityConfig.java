package org.cris6h16.Config.SpringBoot.Security;

import org.cris6h16.Config.SpringBoot.Security.Filters.JwtAuthenticationFilter;
import org.cris6h16.Models.ERoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// todo: tests for this and add cors config
@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${controller.path.core}" +
            "${controller.path.authentication.core}" +
            "${controller.path.authentication.signup}"
    )
    protected String signupPath;

    @Value("${controller.path.core}" +
            "${controller.path.authentication.core}" +
            "${controller.path.authentication.login}"
    )
    protected String loginPath;

    @Value("${controller.path.core}" +
            "${controller.path.user.core}" +
            "${controller.path.user.account.core}"
    )
    protected String userAccountPath;

    @Value("${controller.path.core}" +
            "${controller.path.user.core}" +
            "${controller.path.user.account.core}" +
            "${controller.path.user.account.all.core}"
    )
    protected String allUsersPagePath;

    @Value("${web-front.core}")
    protected String frontEndUrl;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(loginPath, signupPath).permitAll()
                                .requestMatchers(userAccountPath).hasAuthority(ERoles.ROLE_USER.toString())
                                .requestMatchers(allUsersPagePath).hasAuthority(ERoles.ROLE_ADMIN.toString())
                                .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


    // todo: docs about the transactional outbox pattern for the email verification


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontEndUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
//        configuration.setAllowedHeaders(List.of("Content-Type"));
//        configuration.setAllowCredentials(true);
//        configuration.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
