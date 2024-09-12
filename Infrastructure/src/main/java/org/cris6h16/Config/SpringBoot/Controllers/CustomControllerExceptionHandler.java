package org.cris6h16.Config.SpringBoot.Controllers;

import org.cris6h16.Exceptions.AbstractApplicationException;
import org.cris6h16.Exceptions.Impls.EmailNotVerifiedException;
import org.cris6h16.Exceptions.Impls.Rest.MyResponseStatusException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerExceptionHandler {
    @ExceptionHandler(MyResponseStatusException.class)
    public ResponseEntity<String> handleMyResponseStatusException(MyResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatus())
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), e.getStatus()));
    }

    private String buildMessage(String message, HttpStatus status) {
        return """
                {
                    "message": "%msg",
                    "status": "%sts"
                }
                """
                .replace(" ", "")
                .replace("\n", "")
                .replace("%msg", message)
                .replace("%sts", status.toString());
    }

}
