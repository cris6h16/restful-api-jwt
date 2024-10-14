package org.cris6h16.Adapters.Out.SpringData;

import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_Success() {
        // Arrange
        UserModel um = getUserModel();
        UserEntity userEntity = getUserEntity();

        when(userJpaRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);

        // Act
        UserModel result = userRepository.save(um);

        // Assert
        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        verify(userJpaRepository).save(argThat(args ->
                assertEqualsUserModelAndEntity(args, um) // model is mapped to entity, Im verifying all vals are the same ( correctly mapped )
        ));
        assertEqualsUserModelAndEntity(userEntity, result); // jpa.save return an entity, this is mapped to a model, im verigying it was correctly mapped
    }

    private boolean assertEqualsUserModelAndEntity(UserEntity userEntity, UserModel userModel) {
        assertEquals(userModel.getId(), userEntity.getId());
        assertEquals(userModel.getUsername(), userEntity.getUsername());
        assertEquals(userModel.getEmail(), userEntity.getEmail());
        assertEquals(userModel.getPassword(), userEntity.getPassword());
        assertEquals(userModel.getActive(), userEntity.getActive());
        assertEquals(userModel.getEmailVerified(), userEntity.getEmailVerified());
        assertEquals(userModel.getRoles(), userEntity.getRoles());
        assertEquals(userModel.getLastModified(), userEntity.getLastModified());

        return true;
    }


    @Test
    void save_IdExists_ThrowsException() {
        // Arrange
        UserModel userModel = getUserModel();
        when(userJpaRepository.existsById(userModel.getId())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateKeyException.class, () -> userRepository.save(userModel));
        verify(userJpaRepository).existsById(userModel.getId());
    }

    @Test
    void save_UsernameExists_ThrowsException() {
        // Arrange
        UserModel userModel = getUserModel();
        when(userJpaRepository.existsByUsername(userModel.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateKeyException.class, () -> userRepository.save(userModel));
        verify(userJpaRepository).existsByUsername(userModel.getUsername());
    }

    @Test
    void save_EmailExists_ThrowsException() {
        // Arrange
        UserModel userModel = getUserModel();
        when(userJpaRepository.existsByEmail(userModel.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateKeyException.class, () -> userRepository.save(userModel));
        verify(userJpaRepository).existsByEmail(userModel.getEmail());
    }

    @Test
    void existsByUsername() {
        // Arrange
        String username = "cris6h16";
        when(userJpaRepository.existsByUsername(username)).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByUsername(username);

        // Assert
        assertTrue(exists);
        verify(userJpaRepository).existsByUsername(username);
    }

    @Test
    void existsByEmail() {
        // Arrange
        String email = "cristianmherrera21@gmail.com";
        when(userJpaRepository.existsByEmail(email)).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByEmail(email);

        // Assert
        assertTrue(exists);
        verify(userJpaRepository).existsByEmail(email);
    }

    @Test
    void existsById() {
        // Arrange
        Long id = 1L;
        when(userJpaRepository.existsById(id)).thenReturn(true);

        // Act
        boolean exists = userRepository.existsById(id);

        // Assert
        assertTrue(exists);
        verify(userJpaRepository).existsById(id);
    }

    @Test
    void findByEmail() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = getUserEntity();
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Act
        Optional<UserModel> result = userRepository.findByEmail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userEntity.getEmail(), result.get().getEmail());
        verify(userJpaRepository).findByEmail(email);
    }

    @Test
    void findById() {
        // Arrange
        Long id = 1L;
        UserEntity userEntity = getUserEntity();
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(userEntity));

        // Act
        Optional<UserModel> result = userRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userEntity.getId(), result.get().getId());
        verify(userJpaRepository).findById(id);
    }

    @Test
    void updateEmailVerifiedById() {
        Long id = 1L;
        boolean isVerified = true;

        userRepository.updateEmailVerifiedById(id, isVerified);

        verify(userJpaRepository).updateEmailVerifiedById(id, isVerified);
    }

    @Test
    void testDeactivate() {
        Long id = 1L;

        userRepository.deactivate(id);

        verify(userJpaRepository).deactivateById(id);
    }

    @Test
    void updateUsernameById() {
        Long id = 1L;
        String newUsername = "newUsername";

        userRepository.updateUsernameById(id, newUsername);

        verify(userJpaRepository).updateUsernameById(id, newUsername);
    }

    @Test
    void findPasswordById() {
        // Arrange
        Long id = 1L;
        String password = "hashedPassword";
        when(userJpaRepository.findByPasswordById(id)).thenReturn(Optional.of(password));

        // Act
        Optional<String> result = userRepository.findPasswordById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(password, result.get());
        verify(userJpaRepository).findByPasswordById(id);
    }

    @Test
    void updatePasswordById() {
        // Arrange
        Long id = 1L;
        String newPassword = "newHashedPassword";

        // Act
        userRepository.updatePasswordById(id, newPassword);

        // Assert
        verify(userJpaRepository).updatePasswordById(id, newPassword);
    }

    @Test
    void findPage() {
        // Arrange
        MyPageable pageable = new MyPageable(
                0,
                10,
                List.of(
                        new MySortOrder("username", MySortOrder.MyDirection.ASC),
                        new MySortOrder("hello-word-property", MySortOrder.MyDirection.DESC),
                        new MySortOrder("email", MySortOrder.MyDirection.DESC)
                )
        );
        Page<UserEntity> page = mock(Page.class);

        when(userJpaRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        MyPage<UserModel> result = userRepository.findPage(pageable);

        // Assert
        assertNotNull(result);
        verify(userJpaRepository).findAll(argThat((Pageable pag) -> {
            assertEquals(0, pag.getPageNumber());
            assertEquals(10, pag.getPageSize());

            for (Sort.Order order : pag.getSort()) {
                switch (order.getProperty()) {
                    case "username":
                        assertEquals(Sort.Direction.ASC, order.getDirection());
                        break;
                    case "hello-word-property":
                        assertEquals(Sort.Direction.DESC, order.getDirection());
                        break;
                    case "email":
                        assertEquals(Sort.Direction.DESC, order.getDirection());
                        break;
                    default:
                        fail("unexpected property: " + order.getProperty());
                }
            }
            return true;
        }));
    }

    @Test
    void updateEmailById() {
        Long id = 1L;
        String newEmail = "new@email.com";

        userRepository.updateEmailById(id, newEmail);

        verify(userJpaRepository).updateEmailById(id, newEmail);
    }

    @Test
    void getRolesById() {
        Long id = 1L;
        Set<ERoles> roles = Set.of(ERoles.ROLE_USER);
        when(userJpaRepository.findRolesById(id)).thenReturn(roles);

        // Act
        Set<ERoles> result = userRepository.getRolesById(id);

        // Assert
        assertNotNull(result);
        assertEquals(roles, result);
        verify(userJpaRepository).findRolesById(id);
    }

    private UserModel getUserModel() {
        return new UserModel.Builder()
                .setId(1L)
                .setUsername("cris6h16")
                .setPassword("hashed")
                .setEmail("test@example.com")
                .setRoles(Set.of(ERoles.ROLE_USER))
                .setActive(true)
                .setEmailVerified(false)
                .setLastModified(null)
                .build();
    }

    private UserEntity getUserEntity() {
        return UserEntity.builder()
                .id(1L)
                .username("testUser")
                .password("hashed")
                .email("test@example.com")
                .roles(Set.of(ERoles.ROLE_USER))
                .active(true)
                .emailVerified(false)
                .lastModified(null)
                .build();
    }

    @Test
    void findEmailById(){
        Long id = 6541L;
        Optional<String> op = Optional.of("email@gmail.com");
        when(userJpaRepository.findEmailById(id)).thenReturn(op);

        Optional<String> result = userRepository.findEmailById(id);

        assertEquals(result, op);
        verify(userJpaRepository, times(1)).findEmailById(id);
    }
}
