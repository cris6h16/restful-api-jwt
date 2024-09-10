package org.cris6h16.Config.SpringBoot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"org.cris6h16.*"})
@EnableJpaRepositories(basePackages = {"org.cris6h16.*"})
@EntityScan(basePackages = {"org.cris6h16.*"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
