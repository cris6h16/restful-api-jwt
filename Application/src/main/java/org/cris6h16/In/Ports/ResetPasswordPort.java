package org.cris6h16.In.Ports;

public interface ResetPasswordPort {
    void handle(Long id, String password);
}
