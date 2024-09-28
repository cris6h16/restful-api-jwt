package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ErrorMessagesImpl implements ErrorMessages {

    @Value("${error.messages.user.unique.username}")
    private String USERNAME_ALREADY_EXISTS_MSG;

    @Value("${error.messages.user.unique.email}")
    private String EMAIL_ALREADY_EXISTS_MSG;

    @Value("${error.messages.user.invalid.username.length}")
    private String USERNAME_LENGTH_FAIL_MSG;

    @Value("${error.messages.user.invalid.password.length.tooShort}")
    private String PASSWORD_TOO_SHORT_MSG;

    @Value("${error.messages.user.invalid.email}")
    private String EMAIL_INVALID_MSG;

    @Value("${error.messages.user.invalid.id.null-val}")
    private String ID_CANNOT_BE_NULL_MSG;

    @Value("${error.messages.user.invalid.roles.empty}")
    private String ROLES_CANNOT_BE_EMPTY_MSG;

    @Value("${error.messages.user.not-found}")
    private String USER_NOT_FOUNT_MSG;

    @Value("${error.messages.user.email-not-verified}")
    private  String EMAIL_NOT_VERIFIED_MSG ;

    @Value("${error.messages.user.updating-password.password-not-match}")
    private String PASSWORD_NOT_MATCH_WHEN_UPDATING_PASSWORD_MSG ;

    @Value("${error.messages.user.login.invalid-credentials}")
    private String INVALID_CREDENTIAL_WHEN_LOGIN;

    @Value("${error.messages.system.unexpected}")
    private String UNEXPECTED_ERROR_MSG;

    @Override
    public String getUsernameAlreadyExistsMessage() {
        return USERNAME_ALREADY_EXISTS_MSG;
    }

    @Override
    public String getEmailAlreadyExistsMessage() {
        return EMAIL_ALREADY_EXISTS_MSG;
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
        return USER_NOT_FOUNT_MSG;
    }

    @Override
    public String getEmailNotVerifiedMessage() {
        return EMAIL_NOT_VERIFIED_MSG;
    }

    @Override
    public String getCurrentPasswordNotMacthMessage() {
        return PASSWORD_NOT_MATCH_WHEN_UPDATING_PASSWORD_MSG;
    }

    @Override
    public String getInvalidCredentialsMessage() {
        return INVALID_CREDENTIAL_WHEN_LOGIN;
    }

    @Override
    public String getUnexpectedErrorMessage() {
        return UNEXPECTED_ERROR_MSG;
    }

    @Override
    public String getUsernameLengthFailMessage() {
        return USERNAME_LENGTH_FAIL_MSG;
    }
}
