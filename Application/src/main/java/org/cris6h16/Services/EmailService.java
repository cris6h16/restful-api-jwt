package org.cris6h16.Services;

import org.cris6h16.Models.ERoles;

import java.util.Set;

public interface EmailService {
    void sendVerificationEmail(Long id, Set<ERoles> roles, String email);

    void sendResetPasswordEmail(Long id,  Set<ERoles> roles, String email);

    void sendRequestDeleteAccountEmail(Long id, Set<ERoles> roles, String email);

    void sendRequestUpdateEmail(Long id, Set<ERoles> roles, String email);
}
