package org.cris6h16.In.Ports;

public interface UpdatePasswordPort {
    void handle(Long id, String currentPassword, String newPassword);
}
