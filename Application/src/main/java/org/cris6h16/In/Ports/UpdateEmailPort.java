package org.cris6h16.In.Ports;

public interface UpdateEmailPort {
    void handle(Long id, String email);
}
