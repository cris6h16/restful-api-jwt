package org.cris6h16.Repositories;

import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.PageResult;
import org.cris6h16.Repositories.Page.PageRequest;

import java.util.Optional;

// port
public interface UserRepository {
    UserModel saveCustom(UserModel userModel);

    boolean existsByUsernameCustom(String username);

    boolean existsByEmailCustom(String email);

    boolean existsByIdCustom(Long id);

    void updateEmailVerifiedByIdCustom(Long id, boolean isVerified);

    Optional<UserModel> findByEmailCustom(String email);

    Optional<UserModel> findByIdCustom(Long id);

    void deactivate(Long id);

    void updateUsernameByIdCustom(Long id, String newUsername);

    Optional<String> findPasswordByIdCustom(Long id);

    void updatePasswordByIdCustom(Long id, String newPassword);

    PageResult<UserModel> findPageCustom(PageRequest request);
}
