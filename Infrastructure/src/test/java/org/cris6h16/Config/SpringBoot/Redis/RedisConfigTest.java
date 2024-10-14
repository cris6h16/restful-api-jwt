package org.cris6h16.Config.SpringBoot.Redis;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = RedisConfig.class)
class RedisConfigTest {

    @Autowired
    @Qualifier(value = "getAllPublicProfilesTemplate")
    RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllTemplate;


    @Test
    void getAllPublicProfilesTemplate_usingStringRedisSerializerForKey() {
        assertThat(getAllTemplate.getKeySerializer())
                .isInstanceOf(StringRedisSerializer.class);
    }

    @Test
    void getAllPublicProfilesTemplate_usingGenericJackson2JsonRedisSerializerForValue() {
        assertThat(getAllTemplate.getValueSerializer())
                .isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }

}