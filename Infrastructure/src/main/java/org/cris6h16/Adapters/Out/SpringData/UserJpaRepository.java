package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, UserRepository {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

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
}


//todo: transactional implementation