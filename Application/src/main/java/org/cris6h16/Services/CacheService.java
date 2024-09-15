package org.cris6h16.Services;

import org.cris6h16.Models.UserModel;

public interface CacheService {

    void putUserModelToCache(String string, UserModel userModel);

    void updateEmailVerifiedIfExists(Long id, boolean isVerified);

}

