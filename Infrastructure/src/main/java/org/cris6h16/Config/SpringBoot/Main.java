package org.cris6h16.Config.SpringBoot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:Infrastructure.yml")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
