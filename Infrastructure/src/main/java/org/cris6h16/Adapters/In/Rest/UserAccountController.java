package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${controller.path.core}" +
        "${controller.path.user.core}" +
        "${controller.path.user.account.core}"
)
public class UserAccountController {

    private final UserAccountControllerFacade userAccountControllerFacade;

    public UserAccountController(UserAccountControllerFacade userAccountControllerFacade) {
        this.userAccountControllerFacade = userAccountControllerFacade;
    }

    @PostMapping(
            value = "${controller.path.user.account.request.delete}"
    )
    public ResponseEntity<Void> requestDeleteMyAccount() {
        return userAccountControllerFacade.requestDeleteMyAccount();
    }

    @DeleteMapping(
            value = "${controller.path.user.account.request.core}" +
                    "${controller.path.user.account.request.delete}"
    )
    public ResponseEntity<Void> deleteMyAccount() {
        return userAccountControllerFacade.deleteMyAccount();
    }

    @PatchMapping(
            value = "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.username}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyUsername(@RequestBody String newUsername) {
        return userAccountControllerFacade.updateMyUsername(newUsername);
    }

    @PatchMapping(
            value = "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.password}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyPassword(@RequestBody UpdateMyPasswordDTO dto) {
        return userAccountControllerFacade.updateMyPassword(dto);
    }

    @PostMapping(
            value = "${controller.path.user.account.request.core}" +
                    "${controller.path.user.account.request.update-email}"
    )
    public ResponseEntity<Void> requestUpdateMyEmail() {
        return userAccountControllerFacade.requestUpdateMyEmail();
    }

    @PatchMapping(
            value = "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.email}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyEmail(@RequestBody String newEmail) {
        return userAccountControllerFacade.updateMyEmail(newEmail);
    }


    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<PublicProfileDTO> getMyAccount() {
        return userAccountControllerFacade.getMyAccount();
    }

    @GetMapping(
            value = "${controller.path.user.account.all.core}",
            produces = "application/json"
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
