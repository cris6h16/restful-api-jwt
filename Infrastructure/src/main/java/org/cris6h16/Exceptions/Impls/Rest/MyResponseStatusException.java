package org.cris6h16.Exceptions.Impls.Rest;

import org.cris6h16.Exceptions.AbstractInfrastructureException;
import org.springframework.http.HttpStatus;

public class MyResponseStatusException extends AbstractInfrastructureException {
    private final HttpStatus status;

    public MyResponseStatusException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
