package org.cris6h16.Config.SpringBoot.Security;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Properties.ControllerProperties;
import org.cris6h16.Config.SpringBoot.Properties.CorsProperties;
import org.cris6h16.Config.SpringBoot.Security.Filters.JwtAuthenticationFilter;
import org.cris6h16.Models.ERoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

// todo: tests for this and add cors config
@Configuration
@EnableWebSecurity(debug = false)
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ControllerProperties controllerProperties;
    private final CorsProperties corsProperties;

    private static final String ROLE_USER = ERoles.ROLE_USER.toString();
    private static final String ROLE_ADMIN = ERoles.ROLE_ADMIN.toString();


    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ControllerProperties controllerProperties, CorsProperties corsProperties) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.controllerProperties = controllerProperties;
        this.corsProperties = corsProperties;
        log.info("SecurityConfig initialized");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.debug("Building security filter chain");
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.POST, postPermitAllPaths()).permitAll()
                        .requestMatchers(HttpMethod.PUT, verifyEmailPath()).hasAuthority(ROLE_USER)
                        .requestMatchers(HttpMethod.POST, postAndRoleUserPaths()).hasAuthority(ROLE_USER)
                        .requestMatchers(HttpMethod.PATCH, patchAndRoleUserPaths()).hasAuthority(ROLE_USER)
                        .requestMatchers(HttpMethod.DELETE, deleteMyAccountPath()).hasAuthority(ROLE_USER)
                        .requestMatchers(HttpMethod.GET, getMyAccountPath()).hasAuthority(ROLE_USER)
                        .requestMatchers(HttpMethod.GET, allUserPagePath()).hasAuthority(ROLE_ADMIN)
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    private String[] postAndRoleUserPaths() {
        return new String[]{
                extractPath(props -> props.getAuthentication().getRefreshAccessToken()),
                extractPath(props -> props.getAuthentication().getRequestResetPassword()),
                extractPath(props -> props.getUser().getAccount().getRequest().getDelete()),
                extractPath(props -> props.getUser().getAccount().getRequest().getUpdateEmail())
        };
    }

    private String[] patchAndRoleUserPaths() {
        return new String[]{
                extractPath(props -> props.getAuthentication().getResetPassword()),
                extractPath(props -> props.getUser().getAccount().getUpdate().getUsername()),
                extractPath(props -> props.getUser().getAccount().getUpdate().getPassword()),
                extractPath(props -> props.getUser().getAccount().getUpdate().getEmail())
        };
    }
    private String[] postPermitAllPaths() {
        return new String[]{
                extractPath(props -> props.getAuthentication().getLogin()),
                extractPath(props -> props.getAuthentication().getSignup())
        };
    }

    private String getMyAccountPath() {
        return extractPath(props -> props.getUser().getAccount().getCore());
    }


    private String deleteMyAccountPath() {
        return extractPath(props -> props.getUser().getAccount().getCore());
    }


    private String allUserPagePath() {
        return extractPath(props -> props.getUser().getPagination().getAll());
    }

    private String verifyEmailPath() {
        return extractPath(props -> props.getAuthentication().getVerifyEmail());
    }

    String extractPath(Function<ControllerProperties, String> extractor) {
        String path = extractor.apply(controllerProperties);
        log.debug("returning path: {} from {}", path, extractor.toString());
        return path;
    }


    @Bean
    public PasswordEncoder springPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = getCorsConfiguration();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.getPathPattern(), configuration);
        
        log.info("Cors configuration loaded with allowed origins: {}, allowed methods: {}, allowed headers: {}, exposed headers: {}, allow credentials: {}, max age: {}",
                corsProperties.getAllowedOrigins(),
                corsProperties.getAllowedMethods(),
                corsProperties.getAllowedHeaders(),
                corsProperties.getExposedHeaders(),
                corsProperties.isAllowCredentials(),
                corsProperties.getMaxAge()
        );
        return source;
    }

    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setExposedHeaders(corsProperties.getExposedHeaders());
        configuration.setAllowCredentials(corsProperties.isAllowCredentials());
        configuration.setMaxAge(corsProperties.getMaxAge());
        return configuration;
    }
}
