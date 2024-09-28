package org.cris6h16.Utils;

public interface ErrorMessages {
    String getUsernameAlreadyExistsMessage();
    String getEmailAlreadyExistsMessage();

    String getPasswordTooShortMessage();

    String getEmailInvalidMessage();

    String getIdCannotBeNullMessage();

    String getRolesCannotBeEmptyMessage();

    String getUserNotFoundMessage();

    String getEmailNotVerifiedMessage();

    String getCurrentPasswordNotMacthMessage();

    String getInvalidCredentialsMessage();

    String getUnexpectedErrorMessage();

    String getUsernameLengthFailMessage();
}