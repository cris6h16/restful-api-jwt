package org.cris6h16.Adapters.In.Rest;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.In.Rest.Facades.UserControllerFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${controller.path.core}" + "${controller.path.user.core}")
public class UserController {

    private final UserControllerFacade userControllerFacade;

    public UserController(UserControllerFacade userControllerFacade) {
        this.userControllerFacade = userControllerFacade;
    }

    @PostMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.request-delete}"
    )
    public ResponseEntity<Void> requestDeleteMyAccount() {
        return userControllerFacade.requestDeleteMyAccount();
    }

    @DeleteMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.request.core}" +
                    "${controller.path.user.account.request.delete}"
    )
    public ResponseEntity<Void> deleteMyAccount() {
        return userControllerFacade.deleteMyAccount();
    }

    @PatchMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.username}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyUsername(@RequestBody String newUsername) {
        return userControllerFacade.updateMyUsername(newUsername);
    }

    @PatchMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.password}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyPassword(@RequestBody UpdateMyPasswordDTO dto) {
        return userControllerFacade.updateMyPassword(dto);
    }

    @PostMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.request.core}" +
                    "${controller.path.user.account.request.update-email}"
    )
    public ResponseEntity<Void> requestUpdateMyEmail() {
        return userControllerFacade.requestUpdateMyEmail();
    }

    @PatchMapping(
            value = "${controller.path.user.account.core}" +
                    "${controller.path.user.account.update.core}" +
                    "${controller.path.user.account.update.email}",
            consumes = "application/json"
    )
    public ResponseEntity<Void> updateMyEmail(@RequestBody String newEmail) {
        return userControllerFacade.updateMyEmail(newEmail);
    }


    @GetMapping(
            value = "${controller.path.user.account.core}",
            produces = "application/json"
    )
    public ResponseEntity<PublicProfileDTO> getMyAccount() {
        return userControllerFacade.getMyAccount();
    }

    @GetMapping(
            value = "${controller.path.user.account.core}",
            produces = "application/json"
    )
    public ResponseEntity<Page<PublicProfileDTO>> getAllUsers(@PageableDefault(
            size = 50,
            sort = {"id"},
            direction = Sort.Direction.ASC,
            page = 0
    ) Pageable pageable) {
        return userControllerFacade.getAllUsers(pageable);
    }
}
