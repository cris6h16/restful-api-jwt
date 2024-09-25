package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Config.SpringBoot.Services.CacheService;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class UserControllerFacade {

    private final RequestDeleteAccountPort requestDeleteAccountPort;
    private final DeleteAccountPort deleteAccountPort;
    private final UpdateUsernamePort updateUsernamePort;
    private final UpdatePasswordPort updatePasswordPort;
    private final UpdateEmailPort updateEmailPort;
    private final RequestUpdateEmailPort requestUpdateEmailPort;
    private final GetPublicProfilePort getPublicProfilePort;
    private final GetAllPublicProfilesPort getAllPublicProfilesPort;
    private final CacheService cacheService;

    public UserControllerFacade(RequestDeleteAccountPort requestDeleteAccountPort, DeleteAccountPort deleteAccountPort, UpdateUsernamePort updateUsernamePort, UpdatePasswordPort updatePasswordPort, UpdateEmailPort updateEmailPort, RequestUpdateEmailPort requestUpdateEmailPort, GetPublicProfilePort getPublicProfilePort, GetAllPublicProfilesPort getAllPublicProfilesPort, CacheService cacheService) {
        this.requestDeleteAccountPort = requestDeleteAccountPort;
        this.deleteAccountPort = deleteAccountPort;
        this.updateUsernamePort = updateUsernamePort;
        this.updatePasswordPort = updatePasswordPort;
        this.updateEmailPort = updateEmailPort;
        this.requestUpdateEmailPort = requestUpdateEmailPort;
        this.getPublicProfilePort = getPublicProfilePort;
        this.getAllPublicProfilesPort = getAllPublicProfilesPort;
        this.cacheService = cacheService;
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

    // read-only ( non-transactional )
    public ResponseEntity<Page<PublicProfileDTO>> getAllUsers(Pageable pageable) {
        if (pageable == null) throw new IllegalArgumentException("Pageable cannot be null");

        GetAllPublicProfilesCommand cmd = _createCmd(pageable);
        GetAllPublicProfilesOutput output = _getAllPublicProfiles(cmd);
        return ResponseEntity.ok(_createPageImpl(output, pageable)); // todo: if this work as expected, remains a wide refactor
    }

    private Page<PublicProfileDTO> _createPageImpl(GetAllPublicProfilesOutput output, Pageable pageable) {
        List<PublicProfileDTO> profiles = output.getProfiles().stream()
                .map(PublicProfileDTO::new)
                .toList();

        return new PageImpl<>(profiles, pageable, output.getPageItems());
    }

    private GetAllPublicProfilesCommand _createCmd(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream()
                .findFirst()  // first sorting order
                .orElseThrow(() -> new IllegalArgumentException("No sorting order found"));

        return new GetAllPublicProfilesCommand(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                order.getProperty().trim().toLowerCase(),
                order.getDirection().isAscending()
        );
    }

    // caching applied
    private GetAllPublicProfilesOutput _getAllPublicProfiles(GetAllPublicProfilesCommand cmd) {
        GetAllPublicProfilesOutput output = cacheService.getAllUsers(cmd);
        if (output == null) output = getAllPublicProfilesPort.handle(cmd);
        cacheService.putAllUsers(cmd, output);
        return output;
    }
}
