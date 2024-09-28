package org.cris6h16.Config.SpringBoot.Security.UserDetails;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundAuthenticationException extends AuthenticationException {
    public UserNotFoundAuthenticationException(String msg) {
        super(msg);
    }
}
