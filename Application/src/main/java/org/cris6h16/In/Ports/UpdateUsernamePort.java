package org.cris6h16.In.Ports;

public interface UpdateUsernamePort {
    void handle(Long id, String username);
}
