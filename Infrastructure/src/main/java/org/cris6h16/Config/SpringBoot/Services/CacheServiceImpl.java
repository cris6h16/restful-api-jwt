package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Models.UserModel;
import org.cris6h16.Services.CacheService;

import java.util.Optional;

public class CacheServiceImpl implements CacheService {
    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public void putUserModelToCache(String string, UserModel userModel) {

    }

    @Override
    public Optional<UserModel> getByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void updateEmailVerifiedIfExists(Long id, boolean isVerified) {

    }
}
