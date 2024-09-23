package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class InvalidCredentialsException extends AbstractApplicationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
