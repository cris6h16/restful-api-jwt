package org.cris6h16.Config.SpringBoot.Redis;

import org.cris6h16.Config.SpringBoot.Properties.RedisProperty;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class RedisConfigTest {

    @Mock
    RedisProperty redisProperty;

    @InjectMocks
    RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllTemplate;

    @Test
    void getAllPublicProfilesTemplate_keyUsingJackson2JsonRedisSerializer() {
        getAllTemplate = redisConfig.getAllTemplate(mock(RedisConnectionFactory.class));
        assertThat(getAllTemplate.getKeySerializer() ).isInstanceOf(Jackson2JsonRedisSerializer.class);
    }

    @Test
    void getAllPublicProfilesTemplate_ValueUsingJackson2JsonRedisSerializer() {
        getAllTemplate = redisConfig.getAllTemplate(mock(RedisConnectionFactory.class));
        assertThat(getAllTemplate.getValueSerializer() ).isInstanceOf(Jackson2JsonRedisSerializer.class);
    }

    @Test
    void cacheManager_correctInteractionWithProperty(){
        // Arrange
        int minutes = 1234567890;

        when(redisProperty.getTtl()).thenReturn(mock(RedisProperty.Ttl.class));
        when(redisProperty.getTtl().getMinutes()).thenReturn(minutes);

        // Act
        RedisCacheManager cacheManager = redisConfig.cacheManager(new LettuceConnectionFactory());

        // Assert

        verify(redisProperty.getTtl()).getMinutes();
    }
}