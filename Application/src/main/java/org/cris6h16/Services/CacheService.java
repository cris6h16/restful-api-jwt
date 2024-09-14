package org.cris6h16.Services;

import org.cris6h16.Models.UserModel;

import java.util.Optional;

public interface CacheService {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void putUserModelToCache(String string, UserModel userModel);

    Optional<UserModel> getByEmail(String email);

    void updateEmailVerifiedIfExists(Long id, boolean isVerified);

}

