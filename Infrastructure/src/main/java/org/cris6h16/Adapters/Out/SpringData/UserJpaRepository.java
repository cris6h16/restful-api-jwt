package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, UserRepository {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);


    @Override
    default void save(UserModel userModel) {
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
    }

    @Modifying(clearAutomatically = true)/* clearAutomatically = true, to avoid the `EntityManager` to be out of sync ( e.g. In db with emailVerified = false, but in memory with emailVerified = true)*/
    @Query("UPDATE UserEntity u SET u.emailVerified = :isVerified WHERE u.id = :id")
    void updateEmailVerifiedById(Long id, boolean isVerified);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findByEmailInternal(String email);

    @Override
    default Optional<UserModel> findByEmail(String email) {
        UserEntity ue = findByEmailInternal(email).orElse(null);
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
