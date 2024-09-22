package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class UnexpectedException extends AbstractApplicationException {
    public UnexpectedException(String message) {
        super(message);
    }
}
