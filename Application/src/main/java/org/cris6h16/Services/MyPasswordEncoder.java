package org.cris6h16.Services;

public interface MyPasswordEncoder {
    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);
}
