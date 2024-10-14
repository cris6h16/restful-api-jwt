package org.cris6h16.Config.SpringBoot.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperty {
    private String host;
    private int port;
    private Ttl ttl;

    @Getter
    @Setter
    public static class Ttl {
        private int minutes;
    }
}
