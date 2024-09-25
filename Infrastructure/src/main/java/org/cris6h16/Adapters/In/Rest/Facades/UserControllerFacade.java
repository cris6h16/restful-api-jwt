package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class UserControllerFacade {

    private final RequestDeleteAccountPort requestDeleteAccountPort;
    private final DeleteAccountPort deleteAccountPort;
    private final UpdateUsernamePort updateUsernamePort;
    private final UpdatePasswordPort updatePasswordPort;
    private final UpdateEmailPort updateEmailPort;
    private final RequestUpdateEmailPort requestUpdateEmailPort;
    private final GetPublicProfilePort getPublicProfilePort;

    public UserControllerFacade(RequestDeleteAccountPort requestDeleteAccountPort, DeleteAccountPort deleteAccountPort, UpdateUsernamePort updateUsernamePort, UpdatePasswordPort updatePasswordPort, UpdateEmailPort updateEmailPort, RequestUpdateEmailPort requestUpdateEmailPort, GetPublicProfilePort getPublicProfilePort) {
        this.requestDeleteAccountPort = requestDeleteAccountPort;
        this.deleteAccountPort = deleteAccountPort;
        this.updateUsernamePort = updateUsernamePort;
        this.updatePasswordPort = updatePasswordPort;
        this.updateEmailPort = updateEmailPort;
        this.requestUpdateEmailPort = requestUpdateEmailPort;
        this.getPublicProfilePort = getPublicProfilePort;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> requestDeleteMyAccount() {
        requestDeleteAccountPort.handle(Common.getPrincipalId());
        return ResponseEntity.accepted().build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> deleteMyAccount() {
        deleteAccountPort.handle(Common.getPrincipalId());
        return ResponseEntity.noContent().build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> updateMyUsername(String newUsername) {
        updateUsernamePort.handle(Common.getPrincipalId(), newUsername);
        return ResponseEntity.noContent().build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> updateMyPassword(UpdateMyPasswordDTO dto) {
        updatePasswordPort.handle(Common.getPrincipalId(), dto.currentPassword(), dto.newPassword());
        return ResponseEntity.noContent().build();
    }


    // read-only ( non-transactional )
    public ResponseEntity<Void> requestUpdateMyEmail() {
        requestUpdateEmailPort.handle(Common.getPrincipalId());
        return ResponseEntity.accepted().build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<Void> updateMyEmail(String newEmail) {
        updateEmailPort.handle(Common.getPrincipalId(), newEmail);
        return ResponseEntity.noContent().build();
    }

    // read-only ( non-transactional )
    public ResponseEntity<PublicProfileDTO> getMyAccount() {
        GetPublicProfileOutput output = getPublicProfilePort.handle(Common.getPrincipalId());
        return ResponseEntity.ok(new PublicProfileDTO(output));
    }
}
