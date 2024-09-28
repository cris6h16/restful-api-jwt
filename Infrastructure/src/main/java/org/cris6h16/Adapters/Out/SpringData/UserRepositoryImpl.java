package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        Long id = userModel.getId();
        String username = userModel.getUsername();
        String email = userModel.getEmail();

        if (id != null && userJpaRepository.existsById(id)) {
            throw new DuplicateKeyException("Id already exists: " + id);
        }

        if (username != null && userJpaRepository.existsByUsername(username)) {
            throw new DuplicateKeyException("Username already exists: " + username);
        }

        if (email != null && userJpaRepository.existsByEmail(email)) {
            throw new DuplicateKeyException("Email already exists: " + email);
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
        Optional<UserEntity> ue = userJpaRepository.findByEmail(email);
        return ue.map(UserRepositoryImpl::toUserModel);// the mapped if exists else Optinal.empty
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        Optional<UserEntity> op = userJpaRepository.findById(id);
        return op.map(UserRepositoryImpl::toUserModel); // empty Optional if isn't present
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

    static MyPage<UserModel> toUserModelPage(Page<UserEntity> uePage, MyPageable used) {
        List<UserModel> uml = uePage.getContent().stream()
                .map(UserRepositoryImpl::toUserModel)
                .toList();
        return new MyPage<>(
                uePage.getTotalPages(),
                uePage.getTotalElements(),
                used,
                uml
        );
    }

    static Pageable toSpringPageable(MyPageable myPageable) {
        List<Sort.Order> orders = new ArrayList<>(1);
        for (MySortOrder so : myPageable.getSortOrders()) {
            Sort.Order order = new Sort.Order(
                    _toDirection(so.direction()),
                    so.property()
            );
            orders.add(order);
        }
        Sort sort = Sort.by(orders);

        return PageRequest.of(
                myPageable.getPageNumber(),
                myPageable.getPageSize(),
                sort
        );
    }

    static private Sort.Direction _toDirection(MySortOrder.MyDirection dir) {
        return switch (dir) {
            case ASC -> Sort.Direction.ASC;
            case DESC -> Sort.Direction.DESC;
        };
    }

    static UserEntity toUserEntity(UserModel um) {
        return UserEntity.builder()
                .id(um.getId())
                .username(um.getUsername())
                .password(um.getPassword())
                .email(um.getEmail())
                .roles(um.getRoles())
                .active(um.getActive())
                .emailVerified(um.getEmailVerified())
                .lastModified(um.getLastModified())
                .build();
    }

    static UserModel toUserModel(UserEntity e) {
        return new UserModel.Builder()
                .setId(e.getId())
                .setUsername(e.getUsername())
                .setPassword(e.getPassword())
                .setEmail(e.getEmail())
                .setRoles(e.getRoles())
                .setActive(e.getActive())
                .setEmailVerified(e.getEmailVerified())
                .setLastModified(e.getLastModified())
                .build();
    }
}


