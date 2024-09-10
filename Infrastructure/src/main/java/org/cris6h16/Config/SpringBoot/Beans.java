package org.cris6h16.Config.SpringBoot;

import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.In.Ports.CreateAccountPort;
import org.cris6h16.UseCases.CreateAccountUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.cris6h16")
public class Beans {

    @Bean
    public CreateAccountPort createAccountCommand(UserJpaRepository userJpaRepository) {
        return new CreateAccountUseCase(userJpaRepository);
    }

}

