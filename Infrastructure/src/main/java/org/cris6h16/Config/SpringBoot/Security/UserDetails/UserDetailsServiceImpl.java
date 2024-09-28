package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

// this is not a typical impl of UserDetailsServiceImpl due to I have the absolute responsibility for authenticated the requests, I name this similar to that for be able to know easily what this class do just reading the name
@Component
public class UserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserDetailsWithId loadUserById(Long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundAuthenticationException("User not found with id: " + id));

        Set<ERoles> roles = user.getRoles();
        Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

        if (roles != null) {
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.toString()))
                    .toList();
        }

        return new UserDetailsWithId(
                user.getUsername(),
                user.getPassword(),
                user.getActive(),
                true,
                true,
                true,
                authorities,
                user.getId()
        );
    }
}
