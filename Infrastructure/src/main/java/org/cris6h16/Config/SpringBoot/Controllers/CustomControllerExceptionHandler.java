package org.cris6h16.Config.SpringBoot.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.http.HttpStatus;
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

    // HttpMediaTypeNotSupportedException
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }
    //InvalidAttributeException
    @ExceptionHandler(InvalidAttributeException.class)
    public ResponseEntity<String> handleInvalidAttributeException(InvalidAttributeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

    //EmailNotVerifiedException
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<String> handleEmailNotVerifiedException(EmailNotVerifiedException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    //NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.NOT_FOUND));
    }

    //InvalidCredentialsException
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.UNAUTHORIZED));
    }

    //AlreadyExistException
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .header("Content-Type", "application/json")
                .body(buildMessage(e.getMessage(), HttpStatus.CONFLICT));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Unhandled exception in: {}", e.toString());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(buildMessage(errorMessages.getUnexpectedErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
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
