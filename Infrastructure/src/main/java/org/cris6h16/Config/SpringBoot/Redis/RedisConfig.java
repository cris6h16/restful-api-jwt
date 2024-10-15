package org.cris6h16.Config.SpringBoot.Redis;

import org.cris6h16.Config.SpringBoot.Properties.RedisProperty;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;

@Configuration
public class RedisConfig {

    private final RedisProperty redisProperty;

    public RedisConfig(RedisProperty redisProperty) {
        this.redisProperty = redisProperty;
    }

    @Bean(name = "getAllPublicProfilesTemplate")
    RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new Jackson2JsonRedisSerializer<>(GetAllPublicProfilesCommand.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(GetAllPublicProfilesOutput.class));
        return template;
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(redisProperty.getTtl().getMinutes()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }


}
