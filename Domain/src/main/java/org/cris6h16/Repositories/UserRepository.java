package org.cris6h16.Repositories;

import org.cris6h16.Models.UserModel;

// port
public interface UserRepository {
    void save(UserModel userModel);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
