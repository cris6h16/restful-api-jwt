package org.cris6h16.Exceptions.Impls;

import org.cris6h16.Exceptions.AbstractApplicationException;

public class EmailNotVerifiedException extends AbstractApplicationException {
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
