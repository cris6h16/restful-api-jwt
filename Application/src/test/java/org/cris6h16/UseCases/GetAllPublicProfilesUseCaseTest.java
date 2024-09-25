package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.PageRequest;
import org.cris6h16.Repositories.Page.PageResult;
import org.cris6h16.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetAllPublicProfilesUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetAllPublicProfilesUseCase getAllPublicProfilesUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_nullCmdThrowsUnexpectedException() {
        assertThatThrownBy(() -> getAllPublicProfilesUseCase.handle(null))
                .isInstanceOf(UnexpectedException.class)
                .hasMessage("Command cannot be null.");
    }

    @Test
    void handle_emptyPageReturnsEmptyPageResult() {
        // Arrange
        PageResult<UserModel> emptyPage = PageResult.empty();
        GetAllPublicProfilesCommand cmd = createValidCmd();

        when(userRepository.findPageCustom(any(PageRequest.class)))
                .thenReturn(emptyPage);

        // Act
        GetAllPublicProfilesOutput output = getAllPublicProfilesUseCase.handle(cmd);

        // Assert
        assertThat(output.getProfiles()).isEmpty();
        assertThat(output)
                .hasFieldOrPropertyWithValue("pageItems", 0)
                .hasFieldOrPropertyWithValue("totalPages", 0)
                .hasFieldOrPropertyWithValue("isFirstPage", true)
                .hasFieldOrPropertyWithValue("isLastPage", true)
                .hasFieldOrPropertyWithValue("hasNextPage", false)
                .hasFieldOrPropertyWithValue("hasPreviousPage", false);
        assertThat(output.getInput())
                .hasFieldOrPropertyWithValue("page", cmd.getPage())
                .hasFieldOrPropertyWithValue("pageSize", cmd.getPageSize())
                .hasFieldOrPropertyWithValue("sortBy", cmd.getSortBy())
                .hasFieldOrPropertyWithValue("sortDirection", cmd.getSortDirection());
    }

    @Test
    void handle_populatedPageReturnsMappedProfiles() {
        // Arrange
        UserModel user1 = createUserModel(1L, "user1");
        UserModel user2 = createUserModel(2L, "user2");
        List<UserModel> users = List.of(user1, user2);
        PageResult<UserModel> userPage = new PageResult<>(users.size(), 1, true, true, false, false, null, users);
        GetAllPublicProfilesCommand cmd = createValidCmd();

        when(userRepository.findPageCustom(any(PageRequest.class)))
                .thenReturn(userPage);

        // Act
        GetAllPublicProfilesOutput output = getAllPublicProfilesUseCase.handle(cmd);

        // Assert
        assertThat(output)
                .hasFieldOrPropertyWithValue("pageItems", 2)
                .hasFieldOrPropertyWithValue("totalPages", 1)
                .hasFieldOrPropertyWithValue("isFirstPage", true)
                .hasFieldOrPropertyWithValue("isLastPage", true)
                .hasFieldOrPropertyWithValue("hasNextPage", false)
                .hasFieldOrPropertyWithValue("hasPreviousPage", false);

        assertThat(output.getProfiles())
                .hasSize(2);

        assertThat(output.getProfiles().get(0))
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("username", user1.getUsername())
                .hasFieldOrPropertyWithValue("email", user1.getEmail())
                .hasFieldOrPropertyWithValue("roles", user1.getRoles())
                .hasFieldOrPropertyWithValue("active", user1.getActive())
                .hasFieldOrPropertyWithValue("emailVerified", user1.getEmailVerified())
                .hasFieldOrPropertyWithValue("lastModified", user1.getLastModified());

        assertThat(output.getProfiles().get(1))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("username", user2.getUsername())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("roles", user2.getRoles())
                .hasFieldOrPropertyWithValue("active", user2.getActive())
                .hasFieldOrPropertyWithValue("emailVerified", user2.getEmailVerified())
                .hasFieldOrPropertyWithValue("lastModified", user2.getLastModified());
    }

    @Test
    void handle_pageWithNullItemsHandlesGracefully() {
        // Arrange
        PageResult<UserModel> nullItemsPage = new PageResult<>(0, 1, true, true, false, false, null, null);
        GetAllPublicProfilesCommand cmd = createValidCmd();

        when(userRepository.findPageCustom(any(PageRequest.class)))
                .thenReturn(nullItemsPage);

        // Act
        GetAllPublicProfilesOutput output = getAllPublicProfilesUseCase.handle(cmd);

        // Assert
        assertThat(output.getProfiles()).isEmpty();
    }

    private GetAllPublicProfilesCommand createValidCmd() {
        return new GetAllPublicProfilesCommand(
                1, // page number
                12, // page size
                "id",
                true
        );
    }


    private UserModel createUserModel(Long id, String username) {
        return new UserModel(
                id,
                username,
                "12345678",
                username + "@gmail.com",
                Set.of(ERoles.ROLE_USER),
                true,
                true,
                12345678L);
    }
}
