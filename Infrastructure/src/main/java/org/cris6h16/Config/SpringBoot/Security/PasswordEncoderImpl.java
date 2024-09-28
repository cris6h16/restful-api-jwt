package org.cris6h16.Config.SpringBoot.Security;

import org.cris6h16.Services.MyPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderImpl implements MyPasswordEncoder {

    private final PasswordEncoder encoder;

    public PasswordEncoderImpl(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
