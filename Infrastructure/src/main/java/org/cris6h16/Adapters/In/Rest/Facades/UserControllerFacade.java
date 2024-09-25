package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.In.Ports.RequestDeleteAccountPort;
import org.springframework.http.ResponseEntity;

public class UserControllerFacade {

    private final RequestDeleteAccountPort requestDeleteAccountPort;

    public UserControllerFacade(RequestDeleteAccountPort requestDeleteAccountPort) {
        this.requestDeleteAccountPort = requestDeleteAccountPort;
    }

    public ResponseEntity<Void> requestDeleteMyAccount() {
        requestDeleteAccountPort.handle(Common.getPrincipalId());
        return ResponseEntity.accepted().build();
    }

    public ResponseEntity<Void> deleteMyAccount() {

        return null;

    }

    public ResponseEntity<Void> updateMyUsername(String newUsername) {
        return null;
    }

    public ResponseEntity<Void> updateMyPassword(String newPassword) {
        return null;
    }

    public ResponseEntity<Void> updateMyEmail(String newEmail) {
        return null;
    }

    public ResponseEntity<Void> requestUpdateMyEmail() {
        return null;
    }

    public ResponseEntity<PublicProfileDTO> getMyAccount() {
        return null;
    }
}
