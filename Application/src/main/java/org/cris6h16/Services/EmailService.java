package org.cris6h16.Services;

public interface EmailService {
    void sendVerificationEmail(Long id, String email);

    void sendResetPasswordEmail(Long id, String email);

    void sendRequestDeleteAccountEmail(Long id, String email);

    void sendRequestUpdateEmail(Long id, String email);
}
