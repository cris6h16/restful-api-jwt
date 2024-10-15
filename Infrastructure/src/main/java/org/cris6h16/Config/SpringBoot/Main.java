package org.cris6h16.Config.SpringBoot;


import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@ComponentScan(basePackages = {"org.cris6h16.*"})
@EnableJpaRepositories(basePackages = {"org.cris6h16.*"})
@EntityScan(basePackages = {"org.cris6h16.*"})
@EnableAsync // at the moment used for email sending
@Slf4j
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository == null || passwordEncoder == null) { // avoid fail in tests
                log.error("userRepository or passwordEncoder is null");
                return;
            }

            UserModel um = new UserModel.Builder()
                    .setId(null)
                    .setUsername("cris6h16")
                    .setEmail("cristianmherrera21@gmail.com")
                    .setActive(true)
                    .setEmailVerified(true)
                    .setPassword(passwordEncoder.encode("12345678"))
                    .setLastModified(LocalDateTime.now())
                    .setRoles(new HashSet<>(Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN)))
                    .build();

            if (userRepository.existsByUsername(um.getUsername()) || userRepository.existsByEmail(um.getEmail())) {
                log.info("User already exists");
                return;
            }

            userRepository.save(um);
        };
    }
}
