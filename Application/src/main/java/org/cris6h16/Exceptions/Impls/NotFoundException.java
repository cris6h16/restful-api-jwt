package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class NotFoundException extends AbstractApplicationException {
    public NotFoundException(String message) {
        super(message);
    }
}
