package org.cris6h16.Services;

import org.cris6h16.Models.UserModel;

public interface EmailService {
    void sendEmail(String email, String subject, String text, boolean isHTML);

    void sendAsychVerificationEmail(UserModel userModel);
}
