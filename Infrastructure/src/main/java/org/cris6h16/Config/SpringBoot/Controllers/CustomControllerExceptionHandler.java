package org.cris6h16.Config.SpringBoot.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomControllerExceptionHandler {

    private final ErrorMessages errorMessages;

    public CustomControllerExceptionHandler(ErrorMessages errorMessages) {
        this.errorMessages = errorMessages;
    }

    //InvalidAttributeException
    @ExceptionHandler(InvalidAttributeException.class)
    public ResponseEntity<String> handleInvalidAttributeException(InvalidAttributeException e) {
        log.debug("InvalidAttributeException: {}", e.toString());
        return buildResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //EmailNotVerifiedException
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<String> handleEmailNotVerifiedException(EmailNotVerifiedException e) {
        log.debug("EmailNotVerifiedException: {}", e.toString());

        return buildResponseEntity(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        log.debug("NotFoundException: {}", e.toString());

        return buildResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    //InvalidCredentialsException
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException e) {
        log.debug("InvalidCredentialsException: {}", e.toString());

        return buildResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    //AlreadyExistException
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException e) {
        log.debug("AlreadyExistsException: {}", e.toString());

        return buildResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unhandled exception in: {}", e.toString());
        e.printStackTrace();

        return buildResponseEntity(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<String> buildResponseEntity(String message, HttpStatus httpStatus) {
        return ResponseEntity
                .status(httpStatus)
                .header("Content-Type", "application/json")
                .body(buildMessage(message, httpStatus));
    }

    private String buildMessage(String message, HttpStatus status) {
        if (message == null) {
            message = "";
            log.debug("Message is null");
        }

        String template = """
                {
                    "message": "%s",
                    "status": "%s"
                }
                """;
        template = template.replaceAll("\\s+", ""); // remove all whitespaces ( \n, " ", \t, etc)

        return String.format(template, message, status);
    }

}
