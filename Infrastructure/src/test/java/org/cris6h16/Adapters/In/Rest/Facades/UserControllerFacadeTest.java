package org.cris6h16.Adapters.In.Rest.Facades;

import org.cris6h16.Adapters.In.Rest.DTOs.PublicProfileDTO;
import org.cris6h16.Adapters.In.Rest.DTOs.UpdateMyPasswordDTO;
import org.cris6h16.Config.SpringBoot.Security.UserDetails.UserDetailsWithId;
import org.cris6h16.Config.SpringBoot.Services.CacheService;
import org.cris6h16.In.Ports.*;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.In.Results.GetPublicProfileOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerFacadeTest {

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
    private UserControllerFacade userControllerFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void requestDeleteMyAccount_success() {
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
    void deleteMyAccount_success() {
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
    void updateMyUsername_success() {
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
    void updateMyPassword_success() {
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
    void requestUpdateMyEmail_success() {
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
    void updateMyEmail_success() {
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
    void getMyAccount_success() {
        // Arrange
        UserModel user = new UserModel.Builder()
                .setId(97123L)
                .setUsername("cris6h16")
                .setEmail("cristianmherrera21@gmail.com")
                .setPassword("12345678")
                .setRoles(Set.of(ERoles.ROLE_USER))
                .setActive(true)
                .setLastModified(System.currentTimeMillis())
                .setEmailVerified(true)
                .build();
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

    private UserDetailsWithId mockPrincipalId(Long id) {
        Authentication a = mock(Authentication.class);
        UserDetailsWithId user = mock(UserDetailsWithId.class);
        when(user.getId()).thenReturn(id);
        when(a.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(a);
        return user;
    }

    @Test
    void getAllUsers_pageableNullThenIllegalArgumentException() {
        // Arrange
        Pageable pageable = null;

        // Act & Assert
        assertThatThrownBy(() -> userControllerFacade.getAllUsers(pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pageable cannot be null");
    }

    @Test
    void getAllUsers_cacheHitThenDoesntCallThePort() {
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
    void getAllUsers_cacheMissThenCallsThePortAndPutInCacheAndSuccessful() {
        // Arrange;
        long inThisPageCount = 8L;
        long mockTotalElementsAllCount = 18L;

        Pageable pageable = createPageable(1, 10,
                Sort.Order.asc("username"),
                Sort.Order.desc("id"),
                Sort.Order.asc("anyOtherProperty")
        );

        List<GetPublicProfileOutput> inThisPageItems = listOfPublicProfilesOutput(inThisPageCount);
        GetAllPublicProfilesOutput output = createMockGetAllPublicProfilesOutput(mockTotalElementsAllCount, inThisPageItems);

        when(cacheService.getAllUsers(any())).thenReturn(null);
        when(getAllPublicProfilesPort.handle(any())).thenReturn(output);


        // Act
        ResponseEntity<Page<PublicProfileDTO>> res = userControllerFacade.getAllUsers(pageable);

        // Assert
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<PublicProfileDTO> page = res.getBody();
        assertEquals(mockTotalElementsAllCount, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(1, page.getNumber());
        assertEquals(inThisPageCount, page.getNumberOfElements());
        assertEquals(10, page.getSize());
        assertEquals(pageable, page.getPageable());
        assertEquals(pageable.getSort(), page.getSort());
        for (int i = 0; i < 8; i++) {
            PublicProfileDTO dto = page.getContent().get(i);
            GetPublicProfileOutput item = inThisPageItems.get(i);
            assertEquals(dto, new PublicProfileDTO(item));
        }

        verify(cacheService, times(1)).getAllUsers(any());
        verify(getAllPublicProfilesPort, times(1)).handle(argThat(arg -> {
            assertEquals(arg.getPageNumber(), pageable.getPageNumber());
            assertEquals(arg.getPageSize(), pageable.getPageSize());
            assertEquals(arg.getMySortOrders().size(), pageable.getSort().get().count());

            for (int i = 0; i < pageable.getSort().stream().count(); i++) {
                MySortOrder order = arg.getMySortOrders().get(i);
                Sort.Order springOrder = pageable.getSort().stream().toList().get(i);

                assertEquals(order.direction().toString().toLowerCase(), springOrder.getDirection().toString().toLowerCase()); // both were named: ASC & DESC
                assertEquals(order.property(), springOrder.getProperty());
            }
            return true;
        }));
        verify(cacheService, times(1)).putAllUsers(any(), eq(output));
    }

    private GetAllPublicProfilesOutput createMockGetAllPublicProfilesOutput(long totalElementsAll, List<GetPublicProfileOutput> items) {
        return new GetAllPublicProfilesOutput(
                totalElementsAll,
                items
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
                    .setLastModified(123456789L)
                    .setEmailVerified(true)
                    .build();

            GetPublicProfileOutput output = new GetPublicProfileOutput(user);
            list.add(output);
        }
        return list;
    }


    private Pageable createPageable() {
        return PageRequest.of(
                10,
                20,
                Sort.by(Sort.Order.asc("username"))
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