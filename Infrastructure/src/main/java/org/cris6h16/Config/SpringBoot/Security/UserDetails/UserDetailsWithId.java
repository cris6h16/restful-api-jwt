package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserDetailsWithId extends User {
    private final Long id;

    public UserDetailsWithId(Long id, Collection<? extends GrantedAuthority> authorities) {
        super(
                "default-username",
                "default-password",
                true,
                true,
                true,
                true,
                authorities
        );
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}