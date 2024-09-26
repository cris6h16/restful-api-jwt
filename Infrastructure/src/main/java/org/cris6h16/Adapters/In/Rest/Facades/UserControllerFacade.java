package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Config.SpringBoot.Services.CacheService;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.cris6h16.Repositories.Page.MySortOrder.MyDirection.ASC;
import static org.cris6h16.Repositories.Page.MySortOrder.MyDirection.DESC;


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
        return ResponseEntity.ok(_createPageImpl(output, pageable));
    }

    private Page<PublicProfileDTO> _createPageImpl(GetAllPublicProfilesOutput output, Pageable pageable) {
        List<PublicProfileDTO> profiles = output.getItems().stream()
                .map(PublicProfileDTO::new) // GetPublicProfileOutput --> PublicProfileDTO
                .toList();

        return new PageImpl<>(profiles, pageable, output.getTotalElements());
    }

    private GetAllPublicProfilesCommand _createCmd(Pageable springPageable) {
        List<MySortOrder> mySortOrders = new ArrayList<>(1);

        // due to order by multiples properties ( e.g. sort DESC by last_modified  & ASC by Id & DES by username & ...)
        for (Sort.Order springOrder : springPageable.getSort()) {
            Sort.Direction springDirection = springOrder.getDirection();

            // spring order to order in my domain
            MySortOrder.MyDirection myDirection = switch (springDirection) {
                case ASC -> ASC;
                case DESC -> DESC;
            };

            MySortOrder current = new MySortOrder(springOrder.getProperty(), myDirection);
            mySortOrders.add(current);
        }

        return new GetAllPublicProfilesCommand(
                springPageable.getPageNumber(),
                springPageable.getPageSize(),
                mySortOrders
        );
    }

    // caching applied
    private GetAllPublicProfilesOutput _getAllPublicProfiles(GetAllPublicProfilesCommand cmd) {
        GetAllPublicProfilesOutput output = cacheService.getAllUsers(cmd);
        if (output == null) {
            output = getAllPublicProfilesPort.handle(cmd);
            cacheService.putAllUsers(cmd, output);
        }
        return output;
    }
}
