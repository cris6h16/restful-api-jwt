package org.cris6h16.Services;

public interface EmailService {
    void sendEmail(String email, String subject, String text, boolean isHTML);

    // todo: if in yaml i put email.asyc: true i send it asych (non-blocking)
    void sendVerificationEmail(Long id, String email);

    void sendResetPasswordEmail(Long id, String email);

    void sendRequestDeleteAccountEmail(Long id, String email);

    void sendRequestUpdateEmail(Long id, String email);
}
