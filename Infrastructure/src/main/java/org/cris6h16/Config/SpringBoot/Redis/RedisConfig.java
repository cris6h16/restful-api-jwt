package org.cris6h16.Config.SpringBoot.Redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;

@Configuration
//@EnableTransactionManagement
public class RedisConfig {

    @Bean
    LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setEnableTransactionSupport(true);
        return template;
    }


    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1)) //todo: change
                .disableCachingNullValues();
//                .enableTimeToIdle();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
//                .transactionAware()
                .build();
    }

//    @Bean
//    public PlatformTransactionManager transactionManager(DataSource dataSource) throws SQLException {
//        return new DataSourceTransactionManager(dataSource);
//    }
    /*
    considerations:
    - read operations must be run out of transactions
    - read a value set in within a transaction will return null until the transaction is committed ( queued )
     */

}

// todo: add super class audited to User entity