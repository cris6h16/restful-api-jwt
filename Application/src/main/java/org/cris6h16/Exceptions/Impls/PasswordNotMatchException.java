package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class PasswordNotMatchException extends AbstractApplicationException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}

