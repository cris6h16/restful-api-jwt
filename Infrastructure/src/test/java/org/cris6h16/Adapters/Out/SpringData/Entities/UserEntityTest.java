package org.cris6h16.Adapters.Out.SpringData.Entities;

import org.cris6h16.Adapters.Out.SpringData.UserJpaRepository;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

// principally tested the constraints
@SpringBootTest(classes = Math.class)
@ActiveProfiles("test")
public class UserEntityTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    @Test
    void username_unique() {
        // Arrange
        UserEntity u1 = createUserEntity();
        userJpaRepository.save(u1);

        UserEntity u2 = createUserEntity(null, u1.getUsername(), "a@a.com");

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Key (username)=(cris6h16) already exists.");
    }

    @Test
    void username_null() {
        // Arrange
        UserEntity u2 = createUserEntity(null, null, "a@a.com");

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity.username");
    }

    @Test
    void username_tooLong() {
        // Arrange
        UserEntity u2 = createUserEntity(null, "a".repeat(21), "a@a.com");

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("[ERROR: value too long for type character varying(20)]");
    }

    @Test
    void password_null() {
        // Arrange
        UserEntity u2 = createUserEntity();
        u2.setPassword(null);

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity.password");
    }

    @Test
    void password_tooLong() {
        // Arrange
        UserEntity u2 = createUserEntity();
        u2.setPassword("a".repeat(1001));

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("[ERROR: value too long for type character varying(1000)]");
    }


    @Test
    void email_unique() {
        // Arrange
        UserEntity u1 = createUserEntity();
        userJpaRepository.save(u1);

        UserEntity u2 = createUserEntity(null, "hello_world", u1.getEmail());

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Key (email)=(" + u1.getEmail() + ") already exists.");
    }

    @Test
    void email_null() {
        // Arrange
        UserEntity u2 = createUserEntity();
        u2.setEmail(null);

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity.email");
    }

    @Test
    void email_tooLong() {
        // Arrange
        UserEntity u2 = createUserEntity(null, "cris6h16", "a".repeat(51));

        // Act
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("[ERROR: value too long for type character varying(50)]");
    }

    @Test
    void rolesEager() {
        // Arrange
        UserEntity u = createUserEntity();
        userJpaRepository.save(u);

        // Act
        UserEntity eager = userJpaRepository.findById(u.getId()).orElseThrow();

        // Assert
        eager.getRoles().forEach(Enum::toString); // just test if are accessible
    }

    @Test
    void active_null() {
        // Arrange
        UserEntity u2 = createUserEntity();
        u2.setActive(null);

        // Act & Assert
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity.active");
    }

    private UserEntity createUserEntity() {
        LocalDateTime ldt = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ldt = ldt.minusNanos(ldt.getNano());

        return UserEntity.builder()
                .id(1L)
                .username("cris6h16")
                .email("cristianmherrera21@gmail.com")
                .password("12345678")
                .active(true)
                .emailVerified(true)
                .roles(Set.of(ERoles.ROLE_USER))
                .lastModified(ldt)
                .build();
    }

    @Test
    void lastModified_null() {
        // Arrange
        UserEntity u2 = createUserEntity();
        u2.setLastModified(null);

        // Act & Assert
        assertThatThrownBy(() -> userJpaRepository.save(u2))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : org.cris6h16.Adapters.Out.SpringData.Entities.UserEntity.lastModified");
    }

    @Test
    void equals() {
        // Arrange
        UserEntity u1 = createUserEntity();
        UserEntity u2 = createUserEntity();

        // Act & Assert
        assertEquals(u1, u2);
    }

    @Test
    void notEquals_idDiff() {
        // Arrange
        UserEntity u1 = createUserEntity();
        UserEntity u2 = createUserEntity();
        u2.setId(2L);

        // Act & Assert
        assertNotEquals(u1, u2);
    }


    @Test
    void notEquals_typesDiff() {
        // Arrange
        UserEntity u1 = createUserEntity();
        Object obj = new Object();

        // Act & Assert
        assertNotEquals(u1, obj);
    }

    private UserEntity createUserEntity(Long id, String username, String email) {
        LocalDateTime ldt = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();
        ldt = ldt.minusNanos(ldt.getNano());

        return UserEntity.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("12345678")
                .active(true)
                .emailVerified(true)
                .roles(Set.of(ERoles.ROLE_USER))
                .lastModified(ldt)
                .build();
    }

}

