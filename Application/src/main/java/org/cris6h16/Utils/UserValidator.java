package org.cris6h16.Utils;

import org.cris6h16.Exceptions.Impls.InvalidAttributeException;
import org.cris6h16.Models.ERoles;

import java.util.Set;

public class UserValidator {

    private final ErrorMessages errorMessages;

    public UserValidator(ErrorMessages errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidAttributeException(errorMessages.getUsernameCannotBeBlankMessage());
        }
    }


    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 8) {
            throw new InvalidAttributeException(errorMessages.getPasswordTooShortMessage());
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) { //--> ^ = start of the string, \S = any non-whitespace character, + = one or more, @ = @, \S = any non-whitespace character, + = one or more, \. = ., \S = any non-whitespace character, + = one or more, $ = end of the string
            throw new InvalidAttributeException(errorMessages.getEmailInvalidMessage());
        }
    }

    public void validateRoles(Set<ERoles> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new InvalidAttributeException(errorMessages.getRolesCannotBeEmptyMessage());
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            throw new InvalidAttributeException(errorMessages.getIdCannotBeNullMessage());
        }
    }
}
