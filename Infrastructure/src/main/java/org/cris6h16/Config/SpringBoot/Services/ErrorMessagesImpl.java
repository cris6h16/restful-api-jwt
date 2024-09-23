package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ErrorMessagesImpl implements ErrorMessages {

    @Value("${error.messages.unique.username}")
    private String USERNAME_ALREADY_EXISTS_MSG;

    @Value("${error.messages.unique.email}")
    private String EMAIL_ALREADY_EXISTS_MSG;

    @Value("${error.messages.invalid.username.blank}")
    private String USERNAME_CANNOT_BE_BLANK_MSG;

    @Value("${error.messages.invalid.password.length.tooShort}")
    private String PASSWORD_TOO_SHORT_MSG;

    @Value("${error.messages.invalid.email}")
    private String EMAIL_INVALID_MSG;

    @Value("${error.messages.invalid.id.null-val}")
    private String ID_CANNOT_BE_NULL_MSG;

    @Value("${error.messages.invalid.roles.empty}")
    private String ROLES_CANNOT_BE_EMPTY_MSG;

    @Override
    public String getUsernameAlreadyExistsMessage() {
        return USERNAME_ALREADY_EXISTS_MSG;
    }

    @Override
    public String getEmailAlreadyExistsMessage() {
        return EMAIL_ALREADY_EXISTS_MSG;
    }

    @Override
    public String getUsernameCannotBeBlankMessage() {
        return USERNAME_CANNOT_BE_BLANK_MSG;
    }

    @Override
    public String getPasswordTooShortMessage() {
        return PASSWORD_TOO_SHORT_MSG;
    }

    @Override
    public String getEmailInvalidMessage() {
        return EMAIL_INVALID_MSG;
    }

    @Override
    public String getIdCannotBeNullMessage() {
        return ID_CANNOT_BE_NULL_MSG;
    }

    @Override
    public String getRolesCannotBeEmptyMessage() {
        return  ROLES_CANNOT_BE_EMPTY_MSG;
    }

    @Override
    public String getUserNotFoundMessage() {
        return "";
    }

    @Override
    public String getEmailNotVerifiedMessage() {
        return "";
    }
}
