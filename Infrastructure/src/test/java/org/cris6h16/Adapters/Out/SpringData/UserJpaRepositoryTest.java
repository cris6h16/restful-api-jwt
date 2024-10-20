package org.cris6h16.Adapters.Out.SpringData;

import CommonConfigs.JpaTestConfig;
import org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// todo: doc about how the yaml separation helps us improve test isolation
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {JpaTestConfig.class})
@Transactional(isolation = Isolation.READ_COMMITTED)
@ActiveProfiles("test")
@Tag("with-spring-context")
public class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
        userJpaRepository.flush();
    }

    @Test
    void existsByUsername_Exists() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        assertFalse(userJpaRepository.existsByUsername(ue.getUsername()));

        userJpaRepository.save(ue);

        // Act
        boolean exists = userJpaRepository.existsByUsername(ue.getUsername());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByUsername_NotExists() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();

        // Act
        boolean exists = userJpaRepository.existsByUsername(ue.getUsername());

        // Assert
        assertFalse(exists);
    }


    @Test
    void existsByEmail_Exists() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        assertFalse(userJpaRepository.existsByEmail(ue.getEmail()));

        userJpaRepository.save(ue);

        // Act
        boolean exists = userJpaRepository.existsByEmail(ue.getEmail());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_NotExists() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();

        // Act
        boolean exists = userJpaRepository.existsByEmail(ue.getEmail());

        // Assert
        assertFalse(exists);
    }


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateEmailVerifiedById(boolean isVerified) {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        LocalDateTime lastModifiedBefore = ue.getLastModified();

        userJpaRepository.save(ue);

        // Act
        userJpaRepository.updateEmailVerifiedById(ue.getId(), isVerified);

        // Assert
        UserEntity inDB = userJpaRepository.findById(ue.getId()).orElseThrow();
        assertEquals(isVerified, inDB.getEmailVerified());
        assertThat(lastModifiedBefore).isBefore(inDB.getLastModified());
    }


    private UserEntity createValidUserEntityForCreation() {
        LocalDateTime ldt = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime().minusSeconds(30);// -30 seconds to avoid sleep in the updates to verify is lastModified was updated ( the store of `lastModified` is  local date time with no millis, then i dont wanna sleep at least 1 secs in any update test )
        ldt = ldt.minusNanos(ldt.getNano()); // remove nanos ( about .equals() fails if nanos are different, in db dont store nanos )

        return UserEntity.builder()
                .id(null)
                .username("cris6h16")
                .email("cristianmherrera21@gmail.com")
                .password("12345678")
                .active(true)
                .emailVerified(false)
                .lastModified(ldt)
                .roles(new HashSet<>(List.of(ERoles.ROLE_USER)))
                .build();
    }

    @Test
    void findByEmail_found() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        assertFalse(userJpaRepository.findByEmail(ue.getEmail()).isPresent());

        userJpaRepository.save(ue);

        // Act
        Optional<UserEntity> inDB = userJpaRepository.findByEmail(ue.getEmail());

        // Assert
        assertTrue(inDB.isPresent());
        assertEquals(ue, inDB.get());
    }

    @Test
    void findByEmail_notFound() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();

        // Act
        Optional<UserEntity> inDB = userJpaRepository.findByEmail(ue.getEmail());

        // Assert
        assertFalse(inDB.isPresent());
    }

    @Test
    void deactivateById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        LocalDateTime lastModifiedBefore = ue.getLastModified();
        ue.setActive(true);

        userJpaRepository.save(ue);

        // Act
        userJpaRepository.deactivateById(ue.getId());

        // Assert
        UserEntity inDB = userJpaRepository.findById(ue.getId()).orElseThrow();
        assertFalse(inDB.getActive());
        assertThat(lastModifiedBefore).isBefore(inDB.getLastModified());
    }

    @Test
    void updateUsernameById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        LocalDateTime lastModifiedBefore = ue.getLastModified();

        userJpaRepository.save(ue);

        // Act
        String newUsername = "newUsername";
        userJpaRepository.updateUsernameById(ue.getId(), newUsername);

        // Assert
        UserEntity inDB = userJpaRepository.findById(ue.getId()).orElseThrow();
        assertEquals(newUsername, inDB.getUsername());
        assertThat(lastModifiedBefore).isBefore(inDB.getLastModified());
    }

    @Test
    void findByPasswordById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        assertFalse(userJpaRepository.findByPasswordById(ue.getId()).isPresent());

        userJpaRepository.save(ue);

        // Act
        Optional<String> passInDB = userJpaRepository.findByPasswordById(ue.getId());

        // Assert
        assertTrue(passInDB.isPresent());
        assertEquals(ue.getPassword(), passInDB.get());
    }

    @Test
    void updatePasswordById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        LocalDateTime lastModifiedBefore = ue.getLastModified();

        userJpaRepository.save(ue);

        // Act
        String newPassword = "newPassword";
        userJpaRepository.updatePasswordById(ue.getId(), newPassword);

        // Assert
        UserEntity inDB = userJpaRepository.findById(ue.getId()).orElseThrow();
        assertEquals(newPassword, inDB.getPassword());
        assertThat(lastModifiedBefore).isBefore(inDB.getLastModified());
    }

    @Test
    void updateEmailById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        LocalDateTime lastModifiedBefore = ue.getLastModified();

        userJpaRepository.save(ue);

        // Act
        String newEmail = "mewEmail@example.com";
        userJpaRepository.updateEmailById(ue.getId(), newEmail);

        // Assert
        UserEntity inDB = userJpaRepository.findById(ue.getId()).orElseThrow();
        assertEquals(newEmail, inDB.getEmail());
        assertThat(lastModifiedBefore).isBefore(inDB.getLastModified());
    }

    @Test
    void findRolesById() {
        // Arrange
        UserEntity ue = createValidUserEntityForCreation();
        assertThat(userJpaRepository.findRolesById(ue.getId())).isEmpty();

        userJpaRepository.save(ue);

        // Act
        Set<ERoles> rolesInDB = userJpaRepository.findRolesById(ue.getId());

        // Assert
        assertEquals(ue.getRoles(), rolesInDB);
    }
}