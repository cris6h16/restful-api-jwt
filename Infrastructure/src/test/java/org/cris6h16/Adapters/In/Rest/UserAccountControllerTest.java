package org.cris6h16.Adapters.In.Rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Adapters.In.Rest.Facades.UserAccountControllerFacade;
import org.cris6h16.Config.SpringBoot.Main;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // Avoid load the real database
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters
@ActiveProfiles(value = {"test"})
class UserAccountControllerTest {

    @Value("${controller.path.core}" + "${controller.path.user.core}" + "${controller.path.user.account.core}")
    String mainPath;

    @Value("${controller.path.user.account.request.delete}")
    String requestDeleteMyAccountPath;

    @Value("${controller.path.user.account.request.core}" + "${controller.path.user.account.request.delete}")
    String deleteMyAccountPath;

    @Value("${controller.path.user.account.update.core}" + "${controller.path.user.account.update.username}")
    String updateMyUsernamePath;

    @Value("${controller.path.user.account.update.core}" + "${controller.path.user.account.update.password}")
    String updateMyPasswordPath;

    @Value("${controller.path.user.account.request.core}" + "${controller.path.user.account.request.update-email}")
    String requestUpdateMyEmailPath;

    @Value("${controller.path.user.account.update.core}" + "${controller.path.user.account.update.email}")
    String updateMyEmailPath;

    @Value("${controller.path.user.account.all.core}")
    String getAllUsersPath;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountControllerFacade userAccountControllerFacade;

    @Test
    void requestDeleteMyAccount_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.requestDeleteMyAccount()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + requestDeleteMyAccountPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).requestDeleteMyAccount();
    }

    @Test
    void deleteMyAccount_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.deleteMyAccount()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete(mainPath + deleteMyAccountPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).deleteMyAccount();
    }

    @Test
    void updateMyUsername_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newUsername = "newUsername";
        when(userAccountControllerFacade.updateMyUsername(anyString())).thenReturn(ResponseEntity.ok().build());


        mockMvc.perform(patch(mainPath + updateMyUsernamePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUsername))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyUsername(anyString());
    }

    @Test
    void updateMyUsername_shouldRejectNonJsonContentType() throws Exception {
        String newUsername = "newUsername";
        mockMvc.perform(patch(mainPath + updateMyUsernamePath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(newUsername))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void updateMyPassword_shouldCallFacadeAndReturnStatusOk() throws Exception {
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");
        when(userAccountControllerFacade.updateMyPassword(any(UpdateMyPasswordDTO.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(mainPath + updateMyPasswordPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyPassword(any(UpdateMyPasswordDTO.class));
    }

    @Test
    void updateMyPassword_shouldRejectNonJsonContentType() throws Exception {
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");

        mockMvc.perform(patch(mainPath + updateMyPasswordPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void requestUpdateMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        when(userAccountControllerFacade.requestUpdateMyEmail()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post(mainPath + requestUpdateMyEmailPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).requestUpdateMyEmail();
    }

    @Test
    void updateMyEmail_shouldCallFacadeAndReturnStatusOk() throws Exception {
        String newEmail = "newemail@example.com";
        when(userAccountControllerFacade.updateMyEmail(anyString())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch(mainPath + updateMyEmailPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmail))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).updateMyEmail(anyString());
    }

    @Test
    void updateMyEmail_shouldRejectNonJsonContentType() throws Exception {
        String newEmail = "newemail@example.com";

        mockMvc.perform(patch(mainPath + updateMyEmailPath)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(newEmail))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void getMyAccount_shouldCallFacadeAndReturnProfile() throws Exception {
        PublicProfileDTO profile = new PublicProfileDTO();
        when(userAccountControllerFacade.getMyAccount()).thenReturn(ResponseEntity.ok(profile));

        mockMvc.perform(get(mainPath))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).getMyAccount();
    }

    @Test
    void getAllUsers_shouldCallFacadeAndReturnPageOfProfiles() throws Exception {
        Page<PublicProfileDTO> page = new PageImpl<>(List.of(new PublicProfileDTO()));
        when(userAccountControllerFacade.getAllUsers(any(Pageable.class))).thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get(mainPath + getAllUsersPath)
                        .param("page", "0")
                        .param("size", "50")
                        .param("sort", "id,DESC"))
                .andExpect(status().isOk());

        verify(userAccountControllerFacade).getAllUsers(any(Pageable.class));
    }
}
