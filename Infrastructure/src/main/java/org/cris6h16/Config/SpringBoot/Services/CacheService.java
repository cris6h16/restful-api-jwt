package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

    private final RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate;


    public CacheService(@Qualifier(value = "getAllPublicProfilesTemplate") RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate) {
        this.getAllUsersTemplate = getAllUsersTemplate;
    }

    public GetAllPublicProfilesOutput getAllUsers(GetAllPublicProfilesCommand command) {
        return getAllUsersTemplate.opsForValue().get(command);
    }

    public void putAllUsers(GetAllPublicProfilesCommand command, GetAllPublicProfilesOutput output) {
        getAllUsersTemplate.opsForValue().set(command, output);
    }
}
