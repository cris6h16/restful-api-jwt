package org.cris6h16.Config.SpringBoot.Controllers;

import org.cris6h16.Exceptions.Impls.*;
import org.cris6h16.Utils.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomControllerExceptionHandlerTest {

    @Mock
    private ErrorMessages errorMessages;

    @InjectMocks
    private CustomControllerExceptionHandler customControllerExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleInvalidAttributeException() {
        // Arrange
        InvalidAttributeException iae = new InvalidAttributeException("Username cannot be null etc etc etc ");
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String expectedBody = failBody(iae.getMessage(), status);

        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleInvalidAttributeException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    @Test
    void handleEmailNotVerifiedException() {
        // Arrange
        EmailNotVerifiedException iae = new EmailNotVerifiedException("Your email isn;t verified etc etc ..");
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String expectedBody = failBody(iae.getMessage(), status);

        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleEmailNotVerifiedException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    @Test
    void handleNotFoundException() {
        // Arrange
        NotFoundException iae = new NotFoundException("User not found any more etc etc....");
        HttpStatus status = HttpStatus.NOT_FOUND;
        String expectedBody = failBody(iae.getMessage(), status);

        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleNotFoundException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    @Test
    void handleInvalidCredentialsException() {
        // Arrange
        InvalidCredentialsException iae = new InvalidCredentialsException("Some msg saying invalid credentials");
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String expectedBody = failBody(iae.getMessage(), status);

        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleInvalidCredentialsException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    @Test
    void AlreadyExistsException() {
        // Arrange
        AlreadyExistsException iae = new AlreadyExistsException("email already exists etc etc...");
        HttpStatus status = HttpStatus.CONFLICT;
        String expectedBody = failBody(iae.getMessage(), status);

        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleAlreadyExistsException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    // unhandled exceptions
    @Test
    void handleException() {
        // Arrange
        Exception iae = new Exception("your password 'admin1234' can be used again in 1 hour"); // example of internal exception that we dont wanna expose
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String genericMsg = "please try again later, some unexpected error occurred";
        String expectedBody = failBody(genericMsg, status);

        when(errorMessages.getUnexpectedErrorMessage())
                .thenReturn(genericMsg);


        // Act
        ResponseEntity<String> res = customControllerExceptionHandler.handleException(iae);

        // Assert
        assertEquals(res.getStatusCode(), status);
        assertEquals(res.getHeaders().get("Content-Type").get(0), MediaType.APPLICATION_JSON_VALUE);
        assertEquals(res.getBody(), expectedBody);
    }

    private String failBody(String message, HttpStatus status){
        String template = "{\"message\":\"%s\",\"status\":\"%s\"}";
        return String.format(template, message, status);
    }
}