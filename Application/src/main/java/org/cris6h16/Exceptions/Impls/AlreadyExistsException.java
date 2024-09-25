package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class AlreadyExistsException extends AbstractApplicationException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
