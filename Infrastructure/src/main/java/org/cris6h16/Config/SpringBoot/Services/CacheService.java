package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

    private final RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate;


    public CacheService(RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate) {
        this.getAllUsersTemplate = getAllUsersTemplate;
    }
}
