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

// todo: tests for this and add cors config
@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${controller.path.core}" +
            "${controller.path.authentication.core}" +
            "${controller.path.authentication.signup}"
    )
    private String signupPath;

    @Value("${controller.path.core}" +
            "${controller.path.authentication.core}" +
            "${controller.path.authentication.login}"
    )
    private String loginPath;

    @Value("${controller.path.core}" +
            "${controller.path.user.core}" +
            "${controller.path.user.account.core}"
    )
    private String userAccountPath;

    @Value("${controller.path.core}" +
            "${controller.path.user.core}" +
            "${controller.path.user.account.core}" +
            "${controller.path.user.account.all.core}"
    )
    private String allUsersPagePath;

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

//    @Bean
//    public CorsConfiguration corsConfiguration(){
//
//    }
}
