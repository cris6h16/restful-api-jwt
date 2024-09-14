package org.cris6h16.Config.SpringBoot;

import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.Config.SpringBoot.Security.PasswordEncoderImpl;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsServiceImpl;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.TransactionManager;
import org.cris6h16.UseCases.CreateAccountUseCase;
import org.cris6h16.UseCases.LoginUseCase;
import org.cris6h16.UseCases.VerifyEmailUseCase;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Services.MyPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Beans {

    @Bean
    @Scope("singleton")
    public CreateAccountPort createAccountPort(UserRepository userRepository, MyPasswordEncoder passwordEncoder, EmailService emailService, JwtUtils jwtUtils, TransactionManager transactionManager) {
        return new CreateAccountUseCase(userRepository, passwordEncoder, emailService, jwtUtils, transactionManager);
    }

    @Bean
    @Scope("singleton")
    public VerifyEmailPort verifyEmailPort(UserRepository userRepository, MyPasswordEncoder passwordEncoder, EmailService emailService, JwtUtils jwtUtils, TransactionManager transactionManager) {
        return new VerifyEmailUseCase(userRepository, transactionManager);
    }

    @Bean
    @Scope("singleton")
    public LoginPort loginPort(UserRepository userRepository,
                               MyPasswordEncoder passwordEncoder,
                               JwtUtils jwtUtils,
                               TransactionManager transactionManager,
                               EmailService emailService,
                               @Value("${jwt.expiration.token.refresh.secs}") long refreshTokenExpTimeSecs,
                               @Value("${jwt.expiration.token.access.secs}")  long accessTokenExpTimeSecs) {
        return new LoginUseCase(userRepository, passwordEncoder, jwtUtils, transactionManager, emailService, refreshTokenExpTimeSecs, accessTokenExpTimeSecs);
    }


    @Bean
    @Scope("singleton")
    public MyPasswordEncoder passwordEncoder(PasswordEncoder encoder) {
        return new PasswordEncoderImpl(encoder);
    }

    @Bean
    @Scope("singleton")
    public PasswordEncoder springPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // todo: classify better the beans

    @Bean
    @Scope("singleton")
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }
}

