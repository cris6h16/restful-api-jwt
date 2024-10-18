package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
public class UserAccountController {

    private final UserAccountControllerFacade userAccountControllerFacade;
    public UserAccountController(UserAccountControllerFacade userAccountControllerFacade) {
        this.userAccountControllerFacade = userAccountControllerFacade;
    }

    @PostMapping(value = "${controller.user.account.request.delete}")
    public ResponseEntity<Void> requestDeleteMyAccount() {
        return userAccountControllerFacade.requestDeleteMyAccount();
    }

    @DeleteMapping(value = "${controller.user.account.core}")
    public ResponseEntity<Void> deleteMyAccount() {
        return userAccountControllerFacade.deleteMyAccount();
    }

    @PatchMapping(
            value = "${controller.user.account.update.username}",
            consumes = TEXT_PLAIN_VALUE
    )
    public ResponseEntity<Void> updateMyUsername(@RequestBody String newUsername) {
        return userAccountControllerFacade.updateMyUsername(newUsername);
    }

    @PatchMapping(
            value = "${controller.user.account.update.password}",
            consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> updateMyPassword(@RequestBody UpdateMyPasswordDTO dto) {
        return userAccountControllerFacade.updateMyPassword(dto);
    }

    @PostMapping(value = "${controller.user.account.request.update-email}")
    public ResponseEntity<Void> requestUpdateMyEmail() {
        return userAccountControllerFacade.requestUpdateMyEmail();
    }

    /**
     * @param email the new email for replace your old email
     */
    @PatchMapping(
            value = "${controller.user.account.update.email}" ,
            consumes = TEXT_PLAIN_VALUE
    )
    public ResponseEntity<Void> updateMyEmail(@RequestBody String email) {
        return userAccountControllerFacade.updateMyEmail(email);
    }


    @GetMapping(
            value = "${controller.user.account.core}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PublicProfileDTO> getMyAccount() {
        return userAccountControllerFacade.getMyAccount();
    }

    @GetMapping(
            value = "${controller.user.pagination.all}",
            produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<PublicProfileDTO>> getAllUsers(@PageableDefault(
            size = 50,
            sort = {"id"},
            direction = Sort.Direction.DESC,
            page = 0
    ) Pageable pageable) {
        return userAccountControllerFacade.getAllUsers(pageable);
    }
}
