package CustomConfigs;

import org.cris6h16.Adapters.Out.SpringData.UserRepositoryImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "org.cris6h16.Adapters.Out.SpringData")
@EntityScan(basePackages = "org.cris6h16.Adapters.Out.SpringData.Entities")
@ComponentScan(basePackages = {"org.cris6h16.Adapters.Out.SpringData"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {UserRepositoryImpl.class}))
public class JpaTestConfig {

}
