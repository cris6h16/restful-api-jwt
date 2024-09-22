package org.cris6h16.Services;

public interface EmailService {
    void sendEmail(String email, String subject, String text, boolean isHTML);

    // todo: if in yaml i put email.asyc: true i send it asych (non-blocking)
    void sendVerificationEmail(String username, String email);

    void sendAsychResetPasswordEmail(String username, String email);

    void sendAsychRequestDeleteAccountEmail(String username, String email);

    void sendAsychRequestUpdateEmail(String username, String email);
}
