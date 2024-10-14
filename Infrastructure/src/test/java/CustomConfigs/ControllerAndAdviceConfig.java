package CustomConfigs;

import org.cris6h16.Adapters.In.Rest.Facades.AuthenticationControllerFacade;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Adapters.In.Rest.UserAccountController;
import org.cris6h16.Config.SpringBoot.Controllers.CustomControllerExceptionHandler;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;


/*
@EnableAutoConfiguration due to:
- No primary or single unique constructor found for interface org.springframework.data.domain.Pageable]. ( can't inject a pageable element in a controller)
- doesnt find converters for dtos automatically ( controller ) ( deserialize )
- 'application/json' no supported even when is explicitly define in controllers accept 'application/json'
- etc
 */

/**
 * Custom config for test the controller in isolation, this class mock the dependencies of controllers and advice, this
 * for avoid load dependencies in the context
 *
 * @author <a href="https://www.github.com/cris6h16" target="_blank">cris6h16</a>
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {UserAccountController.class, CustomControllerExceptionHandler.class})
public class ControllerAndAdviceConfig {

    @Bean
    AuthenticationControllerFacade authenticationControllerFacade() {
        return mock(AuthenticationControllerFacade.class);
    }

    @Bean
    UserAccountControllerFacade userAccountControllerFacade() {
        return mock(UserAccountControllerFacade.class);
    }

    @Bean
    public ErrorMessages errorMessages() {
        return mock(ErrorMessages.class);
    }

}