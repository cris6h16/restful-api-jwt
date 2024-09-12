package org.cris6h16.Repositories;

import org.cris6h16.Models.UserModel;

import java.util.Optional;

// port
public interface UserRepository {
    void save(UserModel userModel);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void updateEmailVerifiedById(Long id, boolean isVerified);

    Optional<UserModel> findByEmail(String email);
}
