package org.cris6h16.Repositories;

import org.cris6h16.Models.UserModel;

import java.util.Optional;

// port
public interface UserRepository {
    void saveCustom(UserModel userModel);

    boolean existsByUsernameCustom(String username);

    boolean existsByEmailCustom(String email);

    boolean existsByIdCustom(Long id);

    void updateEmailVerifiedByIdCustom(Long id, boolean isVerified);

    Optional<UserModel> findByEmailCustom(String email);

    Optional<UserModel> findByIdCustom(Long id);

}
