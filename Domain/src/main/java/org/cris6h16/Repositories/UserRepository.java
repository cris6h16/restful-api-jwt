package org.cris6h16.Repositories;

import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;

import java.util.Optional;
import java.util.Set;

// port
public interface UserRepository {
    UserModel save(UserModel userModel);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    void updateEmailVerifiedById(Long id, boolean isVerified);

    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findById(Long id);

    void deactivate(Long id);

    void updateUsernameById(Long id, String newUsername);

    Optional<String> findPasswordById(Long id);

    void updatePasswordById(Long id, String newPassword);

    MyPage<UserModel> findPage(MyPageable request);

    void updateEmailById(Long id, String email);

    Set<ERoles> getRolesById(Long id);
}
