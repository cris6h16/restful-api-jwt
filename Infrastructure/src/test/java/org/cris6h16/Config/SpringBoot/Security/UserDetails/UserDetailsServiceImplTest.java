package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserById_NotFoundThenAuthenticationException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsServiceImpl.loadUserById(1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User not found with id: 1");

    }

    @Test
    void loadUserById_RolesNullThenEmptyGrantedAuthorities() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(createUserModel(true));

        // Act
        UserDetailsWithId u = userDetailsServiceImpl.loadUserById(1L);

        // Assert
        assertThat(u.getAuthorities()).isEmpty();
    }

    @Test
    void loadUserById_success() {
        // Arrange
        Optional<UserModel> um = createUserModel(false);
        when(userRepository.findById(103L)).thenReturn(um);

        // Act
        UserDetailsWithId u = userDetailsServiceImpl.loadUserById(103L);

        // Assert
        List<String> authorities = u.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        List<String> roles = um.get().getRoles().stream()
                .map(Enum::toString)
                .toList();

        assertThat(authorities).containsAll(roles);
        assertThat(roles).containsAll(authorities);

        verify(userRepository).findById(103L);
    }

    private Optional<UserModel> createUserModel(boolean hasNullRoles) {
        return Optional.of(
                new UserModel.Builder()
                        .setUsername("cris6h16")
                        .setPassword("12345678")
                        .setActive(true)
                        .setId(1L)
                        .setRoles(hasNullRoles ? null : Set.of(ERoles.ROLE_USER))
                        .build()
        );
    }
}