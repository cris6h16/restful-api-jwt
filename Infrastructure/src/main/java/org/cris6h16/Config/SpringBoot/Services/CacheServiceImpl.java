package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Models.UserModel;
import org.cris6h16.Services.CacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void putUserModelToCache(String key, UserModel userModel) {
        redisTemplate.opsForValue().set(key, userModel, Duration.ofHours(1));
    }

    /**
     * Only update in cache if exists
     *
     * @param id
     * @param isVerified
     */
    @Override
    public void updateEmailVerifiedIfExists(Long id, boolean isVerified) {
        Object obj = redisTemplate.opsForValue().get("user:id:" + id);
        if (obj != null) {
            UserModel userModel = (UserModel) obj;
            userModel.setEmailVerified(isVerified);
            redisTemplate.opsForValue().set("user:id:" + id, userModel);
        }
    }
}
