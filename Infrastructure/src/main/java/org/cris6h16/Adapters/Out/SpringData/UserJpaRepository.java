package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository(value = "UserRepository")
public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, UserRepository {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.username = :username")
    boolean existsByUsernameCustom(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.email = :email")
    boolean existsByEmailCustom(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UserEntity u WHERE u.id = :id")
    boolean existsByIdCustom(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserEntity u SET u.emailVerified = :isVerified WHERE u.id = :id")
    void updateEmailVerifiedByIdCustom(Long id, boolean isVerified);

    Optional<UserEntity> findByEmail(String email);

    @Override
    default UserModel saveCustom(UserModel userModel) {
        UserEntity userEntity = UserEntity.builder()
                .id(userModel.getId())
                .username(userModel.getUsername())
                .password(userModel.getPassword())
                .email(userModel.getEmail())
                .roles(userModel.getRoles())
                .active(userModel.getActive())
                .emailVerified(userModel.getEmailVerified())
                .lastModified(userModel.getLastModified())
                .build();

        save(userEntity);
        userModel.setId(userEntity.getId());
        return userModel;
    }


    @Override
    default Optional<UserModel> findByEmailCustom(String email) {
        UserEntity ue = findByEmail(email).orElse(null);
        if (ue == null) return Optional.empty();
        return Optional.of(new UserModel.Builder()
                .setId(ue.getId())
                .setUsername(ue.getUsername())
                .setPassword(ue.getPassword())
                .setEmail(ue.getEmail())
                .setRoles(ue.getRoles())
                .setActive(ue.getActive())
                .setEmailVerified(ue.getEmailVerified())
                .setLastModified(ue.getLastModified())
                .build());
    }

    @Override
    default Optional<UserModel> findByIdCustom(Long id) {
        UserEntity ue = findById(id).orElse(null);
        if (ue == null) return Optional.empty();
        return Optional.of(new UserModel.Builder()
                .setId(ue.getId())
                .setUsername(ue.getUsername())
                .setPassword(ue.getPassword())
                .setEmail(ue.getEmail())
                .setRoles(ue.getRoles())
                .setActive(ue.getActive())
                .setEmailVerified(ue.getEmailVerified())
                .setLastModified(ue.getLastModified())
                .build());
    }


}


//todo: transactional implementation
