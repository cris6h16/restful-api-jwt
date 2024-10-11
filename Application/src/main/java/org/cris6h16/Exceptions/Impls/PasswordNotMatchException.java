package org.cris6h16.Exceptions.Impls;

public class PasswordNotMatchException extends InvalidCredentialsException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}

