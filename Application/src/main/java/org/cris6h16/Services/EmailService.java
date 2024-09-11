package org.cris6h16.Services;

public interface EmailService {
    void sendEmail(String email, String subject, String text, boolean isHTML);
}
