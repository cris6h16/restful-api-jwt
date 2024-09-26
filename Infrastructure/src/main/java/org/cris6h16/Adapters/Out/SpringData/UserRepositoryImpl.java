package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import static org.cris6h16.Adapters.Out.SpringData.Mapper.Entity.toUserEntity;
import static org.cris6h16.Adapters.Out.SpringData.Mapper.Entity.toUserModel;
import static org.cris6h16.Adapters.Out.SpringData.Mapper.Pagination.toSpringPageable;
import static org.cris6h16.Adapters.Out.SpringData.Mapper.Pagination.toUserModelPage;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }


    @Override
    public UserModel save(UserModel userModel) {
        _avoidUpdatesWithTheEntireModel(userModel);
        UserEntity entity = toUserEntity(userModel);
        entity = userJpaRepository.save(entity);

        return toUserModel(entity);
    }

    private void _avoidUpdatesWithTheEntireModel(UserModel userModel) {
        if (userJpaRepository.existsById(userModel.getId())) {
            throw new DuplicateKeyException("Id already exists: " + userModel.getId());
        }

        if (userJpaRepository.existsByUsername(userModel.getUsername())) {
            throw new DuplicateKeyException("Username already exists: " + userModel.getUsername());
        }
        if (userJpaRepository.existsByEmail(userModel.getEmail())) {
            throw new DuplicateKeyException("Email already exists: " + userModel.getEmail());
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public void updateEmailVerifiedById(Long id, boolean isVerified) {
        userJpaRepository.updateEmailVerifiedById(id, isVerified);
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        Optional<UserEntity> op = userJpaRepository.findById(id);
        return op.map(Mapper.Entity::toUserModel); // empty Optional if isn't present
    }

    @Override
    public void deactivate(Long id) {
        userJpaRepository.deactivateById(id);
    }

    @Override
    public void updateUsernameById(Long id, String newUsername) {
        userJpaRepository.updateUsernameById(id, newUsername);
    }

    @Override
    public Optional<String> findPasswordById(Long id) {
        return userJpaRepository.findByPasswordById(id);
    }

    @Override
    public void updatePasswordById(Long id, String newPassword) {
        userJpaRepository.updatePasswordById(id, newPassword);
    }

    @Override
    public MyPage<UserModel> findPage(MyPageable request) {
        Pageable pageable = toSpringPageable(request);
        Page<UserEntity> uePage = userJpaRepository.findAll(pageable);
        return toUserModelPage(uePage, request);
    }

    @Override
    public void updateEmailById(Long id, String email) {
        userJpaRepository.updateEmailById(id, email);
    }

    @Override
    public Set<ERoles> getRolesById(Long id) {
        return userJpaRepository.findRolesById(id);
    }
}
