package org.cris6h16.Config.SpringBoot.Security.UserDetails;
public interface CustomUserDetailsService {
    UserDetailsWithId loadUserById(Long id) throws UserNotFoundAuthenticationException;
}
