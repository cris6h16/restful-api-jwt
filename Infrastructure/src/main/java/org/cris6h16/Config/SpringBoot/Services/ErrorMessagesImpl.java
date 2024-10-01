package org.cris6h16.Config.SpringBoot.Services;

import org.cris6h16.Config.SpringBoot.Properties.ErrorMessagesProperties;
import org.cris6h16.Utils.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ErrorMessagesImpl implements ErrorMessages {

    private final ErrorMessagesProperties errorMessagesProperties;

    public ErrorMessagesImpl(ErrorMessagesProperties errorMessagesProperties) {
        this.errorMessagesProperties = errorMessagesProperties;
    }

    @Override
    public String getUsernameAlreadyExistsMessage() {
        return errorMessagesProperties.getUser().getUnique().getUsername();
    }

    @Override
    public String getEmailAlreadyExistsMessage() {
        return errorMessagesProperties.getUser().getUnique().getEmail();
    }


    @Override
    public String getPasswordTooShortMessage() {
        return errorMessagesProperties.getUser().getInvalid().getPassword().getLength().getTooShort();
    }

    @Override
    public String getEmailInvalidMessage() {
        return errorMessagesProperties.getUser().getInvalid().getEmail();
    }

    @Override
    public String getIdCannotBeNullMessage() {
        return errorMessagesProperties.getUser().getInvalid().getId().getNullVal();
    }

    @Override
    public String getRolesCannotBeEmptyMessage() {
        return errorMessagesProperties.getUser().getInvalid().getRoles().getEmpty();
    }

    @Override
    public String getUserNotFoundMessage() {
        return errorMessagesProperties.getUser().getNotFound();
    }

    @Override
    public String getEmailNotVerifiedMessage() {
        return errorMessagesProperties.getUser().getEmailNotVerified();
    }

    @Override
    public String getCurrentPasswordNotMacthMessage() {
        return errorMessagesProperties.getUser().getUpdatingPassword().getPasswordNotMatch();
    }

    @Override
    public String getInvalidCredentialsMessage() {
        return errorMessagesProperties.getUser().getLogin().getInvalidCredentials();
    }

    @Override
    public String getUnexpectedErrorMessage() {
        return errorMessagesProperties.getSystem().getUnexpected();
    }

    @Override
    public String getUsernameLengthFailMessage() {
        return errorMessagesProperties.getUser().getInvalid().getUsername().getLength();
    }
}
