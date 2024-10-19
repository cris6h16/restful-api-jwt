package org.cris6h16.Config.SpringBoot;

import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

public class MainTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private Main main;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRunner_whenUserDoesNotExist_shouldSaveUser() throws Exception {
        // Arrange
        String username = "cris6h16";
        String email = "cristianmherrera21@gmail.com";
        String rawPassword = "12345678";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        CommandLineRunner runner = main.runner(userRepository, passwordEncoder);

        // Act
        runner.run();

        // Assert
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals(username) &&
                        user.getEmail().equals(email) &&
                        user.getActive() &&
                        user.getEmailVerified() &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getLastModified() != null &&
                        user.getRoles().contains(ERoles.ROLE_USER) &&
                        user.getRoles().contains(ERoles.ROLE_ADMIN)
        ));
    }

    @Test
    void testRunner_whenUserExists_shouldNotSaveUser() throws Exception {
        // Arrange
        String username = "cris6h16";
        String email = "cristianmherrera21@gmail.com";

        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        CommandLineRunner runner = main.runner(userRepository, passwordEncoder);

        // Act
        runner.run();

        // Assert
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void testRunner_whenPasswordEncoderIsNull_shouldNotSaveUser() throws Exception {
        // Arrange
        CommandLineRunner runner = main.runner(userRepository, null);

        // Act
        runner.run();

        // Assert
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void testRunner_whenUserRepositoryIsNull_shouldNotSaveUser() throws Exception {
        // Arrange
        CommandLineRunner runner = main.runner(null, passwordEncoder);

        // Act
        runner.run();

        // Assert
        verify(userRepository, never()).save(any(UserModel.class));
    }
}
