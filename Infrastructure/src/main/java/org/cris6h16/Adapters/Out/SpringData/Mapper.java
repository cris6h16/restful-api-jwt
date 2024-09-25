package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

// package-protected, used just in repo
final class Mapper {
    private Mapper() {
    }

    final class Entity {
        private Entity() {
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

    final class Pagination {
        private Pagination() {
        }

        static MyPage<UserModel> toUserModelPage(Page<UserEntity> uePage, MyPageable used) {
            List<UserModel> uml = uePage.getContent().stream()
                    .map(Entity::toUserModel)
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
    }
}
