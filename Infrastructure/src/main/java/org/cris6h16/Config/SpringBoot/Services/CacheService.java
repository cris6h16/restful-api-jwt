package org.cris6h16.Config.SpringBoot.Services;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CacheService {

    private final RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate;


    public CacheService(@Qualifier(value = "getAllPublicProfilesTemplate") RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> getAllUsersTemplate) {
        this.getAllUsersTemplate = getAllUsersTemplate;
    }

    public GetAllPublicProfilesOutput getAllUsers(GetAllPublicProfilesCommand command) {
        GetAllPublicProfilesOutput output = getAllUsersTemplate.opsForValue().get(command);

        if (output == null) log.debug("No cache found for key: {}", command);
        else {
            log.debug("Cache found for key: {}, cache found: {}", command, output);
        }

        return output;
    }

    public void putAllUsers(GetAllPublicProfilesCommand command, GetAllPublicProfilesOutput output) {
        log.trace("Saving cache key: {}, cache value: {}", command, output);
        getAllUsersTemplate.opsForValue().set(command, output);
    }
}
