package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.ERoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.emailVerified = :isVerified, u.lastModified = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateEmailVerifiedById(Long id, boolean isVerified);

    Optional<UserEntity> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.active = false,u.lastModified = CURRENT_TIMESTAMP  WHERE u.id = :id")
    void deactivateById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.username = :newUsername ,u.lastModified = CURRENT_TIMESTAMP  WHERE u.id = :id")
    void updateUsernameById(Long id, String newUsername);

    @Query("SELECT u.password FROM UserEntity u WHERE u.id = :id")
    Optional<String> findByPasswordById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.password = :newPassword ,u.lastModified = CURRENT_TIMESTAMP  WHERE u.id = :id")
    void updatePasswordById(Long id, String newPassword);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.email = :newEmail ,u.lastModified = CURRENT_TIMESTAMP  WHERE u.id = :id")
    void updateEmailById(Long id, String newEmail);

    @Query("SELECT u.roles  FROM UserEntity u WHERE u.id = :id")
    Set<ERoles> findRolesById(Long id);

}
