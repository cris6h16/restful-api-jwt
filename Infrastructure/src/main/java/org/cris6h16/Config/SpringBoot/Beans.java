package org.cris6h16.Config.SpringBoot;

import org.cris6h16.Config.SpringBoot.Security.PasswordEncoderImpl;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsServiceImpl;
import org.cris6h16.Config.SpringBoot.Services.ErrorMessagesImpl;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.In.Ports.LoginPort;
import org.cris6h16.In.Ports.RequestResetPasswordPort;
import org.cris6h16.In.Ports.VerifyEmailPort;
import org.cris6h16.Repositories.UserRepository;
import org.cris6h16.Services.EmailService;
import org.cris6h16.Services.MyPasswordEncoder;
import org.cris6h16.UseCases.CreateAccountUseCase;
import org.cris6h16.UseCases.LoginUseCase;
import org.cris6h16.UseCases.RequestResetPasswordUseCase;
import org.cris6h16.UseCases.VerifyEmailUseCase;
import org.cris6h16.Utils.ErrorMessages;
import org.cris6h16.Utils.JwtUtils;
import org.cris6h16.Utils.UserValidator;
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
    public ErrorMessages errorMessages() {
        return new ErrorMessagesImpl();
    }

    @Bean
    @Scope("singleton")
    public UserValidator userValidator(ErrorMessages errorMessages) {
        return new UserValidator(errorMessages);
    }



    @Bean
    @Scope("singleton")
    public CreateAccountPort createAccountPort(UserRepository userRepository,
                                               MyPasswordEncoder passwordEncoder,
                                               EmailService emailService,
                                               JwtUtils jwtUtils,
                                               ErrorMessages errorMessages,
                                               UserValidator userValidator) {
        return new CreateAccountUseCase(
                userRepository,
                passwordEncoder,
                emailService,
                jwtUtils,
                errorMessages,
                userValidator
        );
    }

    @Bean
    @Scope("singleton")
    public VerifyEmailPort verifyEmailPort(UserRepository userRepository,
                                           UserValidator userValidator
    ) {
        return new VerifyEmailUseCase(
                userRepository,
                userValidator
        );
    }

    @Bean
    @Scope("singleton")
    public RequestResetPasswordPort requestResetPasswordPort(EmailService emailService,
                                                             UserValidator userValidator,
                                                             UserRepository userRepository) {
        return new RequestResetPasswordUseCase(
                emailService,
                userValidator,
                userRepository
        );
    }

    @Bean
    @Scope("singleton")
    public LoginPort loginPort(UserRepository userRepository,
                               MyPasswordEncoder passwordEncoder,
                               JwtUtils jwtUtils,
                               EmailService emailService,
                                 ErrorMessages errorMessages,
                                 UserValidator userValidator,
                               @Value("${jwt.expiration.token.refresh.secs}") long refreshTokenExpTimeSecs,
                               @Value("${jwt.expiration.token.access.secs}") long accessTokenExpTimeSecs) {
        return new LoginUseCase(
                userRepository,
                passwordEncoder,
                jwtUtils,
                emailService,
                errorMessages,
                userValidator,
                refreshTokenExpTimeSecs,
                accessTokenExpTimeSecs
        );
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

