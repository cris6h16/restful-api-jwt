package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Services.CacheService;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserControllerFacadeTest {

    @Mock
    private RequestDeleteAccountPort requestDeleteAccountPort;
    @Mock
    private DeleteAccountPort deleteAccountPort;
    @Mock
    private UpdateUsernamePort updateUsernamePort;
    @Mock
    private UpdatePasswordPort updatePasswordPort;
    @Mock
    private UpdateEmailPort updateEmailPort;
    @Mock
    private RequestUpdateEmailPort requestUpdateEmailPort;
    @Mock
    private GetPublicProfilePort getPublicProfilePort;
    @Mock
    private GetAllPublicProfilesPort getAllPublicProfilesPort;
    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserAccountControllerFacade userControllerFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void requestDeleteMyAccount_success() {
        // Arrange
        var principalMocked = mockPrincipalId(17L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.requestDeleteMyAccount();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(requestDeleteAccountPort).handle(17L);
    }


    @Test
    public void deleteMyAccount_success() {
        // Arrange
        var principalMocked = mockPrincipalId(756L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.deleteMyAccount();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(deleteAccountPort).handle(756L);
    }

    @Test
    public void updateMyUsername_success() {
        // Arrange
        String newUsername = "cris6h16";
        var principalMocked = mockPrincipalId(756L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyUsername(newUsername);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updateUsernamePort).handle(756L, newUsername);
    }

    @Test
    public void updateMyPassword_success() {
        // Arrange
        UpdateMyPasswordDTO dto = new UpdateMyPasswordDTO("oldPassword", "newPassword");
        var principalMocked = mockPrincipalId(666L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyPassword(dto);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updatePasswordPort).handle(666L, dto.currentPassword(), dto.newPassword());
    }

    @Test
    public void requestUpdateMyEmail_success() {
        // Arrange
        String newEmail = "cristianmherrera21@gmail.com";
        var principalMocked = mockPrincipalId(123L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.requestUpdateMyEmail();

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(requestUpdateEmailPort).handle(123L);
    }

    @Test
    public void updateMyEmail_success() {
        // Arrange
        String newEmail = "cristianmherrera21@gmail.com";
        var principalMocked = mockPrincipalId(600L);

        // Act
        ResponseEntity<Void> res = userControllerFacade.updateMyEmail(newEmail);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res.getBody()).isNull();
        verify(principalMocked).getId();
        verify(updateEmailPort).handle(600L, newEmail);
    }

    @Test
    public void getMyAccount_success() {
        // Arrange
        UserModel user = createUser();
        GetPublicProfileOutput output = new GetPublicProfileOutput(user);
        PublicProfileDTO dto = new PublicProfileDTO(output);
        var principalMocked = mockPrincipalId(97123L);

        when(getPublicProfilePort.handle(97123L)).thenReturn(output);

        // Act
        ResponseEntity<PublicProfileDTO> res = userControllerFacade.getMyAccount();

        // Assert
        verify(principalMocked).getId();
        verify(getPublicProfilePort).handle(97123L);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(dto);
    }

    private UserModel createUser() {
        return new UserModel.Builder()
                .setId(97123L)
                .setUsername("cris6h16")
                .setEmail("cristianmherrera21@gmail.com")
                .setPassword("12345678")
                .setRoles(Set.of(ERoles.ROLE_USER))
                .setActive(true)
                .setLastModified(Instant.now()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime() // Removes timezone info
                )
                .setEmailVerified(true)
                .build();
    }

    private UserDetailsWithId mockPrincipalId(Long id) {
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(id);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);
        return user;
    }

    @Test
    public void getAllUsers_pageableNullThenIllegalArgumentException() {
        // Arrange
        Pageable pageable = null;

        // Act & Assert
        assertThatThrownBy(() -> userControllerFacade.getAllUsers(pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    public void getAllUsers_cacheHitThenDoesntCallThePort() {
        // Arrange
        Pageable pageable = createPageable();
        GetAllPublicProfilesOutput output = mock(GetAllPublicProfilesOutput.class);

        when(cacheService.getAllUsers(any())).thenReturn(output);

        // Act
        ResponseEntity<Page<PublicProfileDTO>> res = userControllerFacade.getAllUsers(pageable);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cacheService, times(1)).getAllUsers(any());
        verify(cacheService, never()).putAllUsers(any(), any());
        verify(getAllPublicProfilesPort, never()).handle(any());
    }

    @Test
    public void getAllUsers_cacheMissThenCallsThePortAndPutInCache() {
        // Arrange;
        GetAllPublicProfilesOutput output = mock(GetAllPublicProfilesOutput.class);

        when(cacheService.getAllUsers(any())).thenReturn(null);
        when(getAllPublicProfilesPort.handle(any())).thenReturn(output);

        // Act
        ResponseEntity<Page<PublicProfileDTO>> res = userControllerFacade.getAllUsers(createPageable());

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cacheService, times(1)).getAllUsers(any());
        verify(getAllPublicProfilesPort, times(1)).handle(any());
        verify(cacheService, times(1)).putAllUsers(any(), any());
    }

    @Test
    public void getAllUsers_cmdMappedCorrectlyToMyPageable() {
        // Arrange;
        GetAllPublicProfilesOutput output = createGetAllOutput();
        Pageable springPageable = createPageable();

        when(cacheService.getAllUsers(any())).thenReturn(null);
        when(getAllPublicProfilesPort.handle(any())).thenReturn(output);

        // Act
        ResponseEntity<Page<PublicProfileDTO>> res = userControllerFacade.getAllUsers(createPageable());

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(getAllPublicProfilesPort, times(1))
                .handle(argThat(myPageable ->
                        myPageable.equals(toMyPageable(springPageable))));
    }

    private GetAllPublicProfilesCommand toMyPageable(Pageable sentPageable) {
        return new GetAllPublicProfilesCommand(
                sentPageable.getPageNumber(),
                sentPageable.getPageSize(),
                toMySortOrders(sentPageable.getSort())
        );
    }

    private List<MySortOrder> toMySortOrders(Sort sort) {
        List<MySortOrder> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            MySortOrder.MyDirection direction = switch (order.getDirection()) {
                case ASC -> MySortOrder.MyDirection.ASC;
                case DESC -> MySortOrder.MyDirection.DESC;
            };

            MySortOrder myOrder = new MySortOrder(order.getProperty(), direction);
            orders.add(myOrder);
        }
        return orders;
    }

    @Test
    public void getAllUsers_outputMappedToSpringPageSuccess() {
        // Arrange;
        Pageable ignoredPageable = createPageable();
        GetAllPublicProfilesOutput output = createGetAllOutput();

        when(cacheService.getAllUsers(any())).thenReturn(null);
        when(getAllPublicProfilesPort.handle(any())).thenReturn(output);

        // Act
        ResponseEntity<Page<PublicProfileDTO>> res = userControllerFacade.getAllUsers(ignoredPageable);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(res.getBody(), toSpringPage(output));
    }

    private Page<PublicProfileDTO> toSpringPage(GetAllPublicProfilesOutput output) {
        return new PageImpl<>(
                output.getItems().stream().map(PublicProfileDTO::new).toList(), // GetPublicProfileOutput --> PublicProfileDTO
                createPageable(output.getCommand()),
                output.getTotalElements()
        );
    }

    private Pageable createPageable(GetAllPublicProfilesCommand command) {
        return PageRequest.of(
                command.getPageNumber(),
                command.getPageSize(),
                createSort(command.getMySortOrders())
        );
    }

    private Sort createSort(List<MySortOrder> mySortOrders) {
        List<Sort.Order> orders = new ArrayList<>(mySortOrders.size());
        for (MySortOrder mySortOrder : mySortOrders) {
            Sort.Order order = switch (mySortOrder.direction()) {
                case ASC -> Sort.Order.asc(mySortOrder.property());
                case DESC -> Sort.Order.desc(mySortOrder.property());
            };
            orders.add(order);
        }
        return Sort.by(orders);
    }

    private GetAllPublicProfilesOutput createGetAllOutput() {
        return new GetAllPublicProfilesOutput(
                18,
                2,
                createCmd(),
                listOfPublicProfilesOutput(10)
        );

    }


    private GetAllPublicProfilesCommand createCmd() {
        return new GetAllPublicProfilesCommand(
                1,
                10,
                List.of(
                        new MySortOrder("username", MySortOrder.MyDirection.ASC),
                        new MySortOrder("id", MySortOrder.MyDirection.DESC),
                        new MySortOrder("anyOtherProperty", MySortOrder.MyDirection.ASC)
                )
        );
    }

    private List<GetPublicProfileOutput> listOfPublicProfilesOutput(long n) {
        List<GetPublicProfileOutput> list = new ArrayList<>((int) n);
        for (int i = 0; i < n; i++) {
            UserModel user = new UserModel.Builder()
                    .setId((long) i)
                    .setUsername("username" + i)
                    .setEmail("email" + i)
                    .setPassword("password" + i)
                    .setRoles(Set.of(ERoles.ROLE_USER))
                    .setActive(true)
                    .setLastModified(Instant.now()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime())  // Removes timezone info
                    .setEmailVerified(true)
                    .build();

            GetPublicProfileOutput output = new GetPublicProfileOutput(user);
            list.add(output);
        }
        return list;
    }


    private Pageable createPageable() {
        return createPageable(1, 10,
                Sort.Order.asc("username"),
                Sort.Order.desc("id"),
                Sort.Order.asc("anyOtherProperty")
        );
    }

    private Pageable createPageable(int pageNumber, int pageSize, Sort.Order... orders) {
        return PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by(orders)
        );
    }

}