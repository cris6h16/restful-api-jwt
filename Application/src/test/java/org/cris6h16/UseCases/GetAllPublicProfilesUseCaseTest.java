package org.cris6h16.UseCases;

import org.cris6h16.Exceptions.Impls.UnexpectedException;
import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.cris6h16.Models.UserModel;
import org.cris6h16.Repositories.Page.MyPage;
import org.cris6h16.Repositories.Page.MyPageable;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.cris6h16.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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
    void handle_commandTranslatedCorrectlyToPageable() {
        // Arrange
        MyPage<UserModel> dummyPage = mock(MyPage.class);
        MyPageable dummyPageable = createPageRequest();
        GetAllPublicProfilesCommand input = createCmd();

        when(userRepository.findPage(any(MyPageable.class))).thenReturn(dummyPage);
        when(dummyPage.getPageRequest()).thenReturn(dummyPageable);

        // Act
        getAllPublicProfilesUseCase.handle(input);

        // Assert
        verify(userRepository).findPage(argThat(req -> {
            assertEquals(input.getPageNumber(), req.getPageNumber());
            assertEquals(input.getPageSize(), req.getPageSize());
            assertEquals(input.getMySortOrders(), req.getSortOrders());
            return true;
        }));
    }

    private GetAllPublicProfilesCommand createCmd() {
        return new GetAllPublicProfilesCommand(
                10,
                90,
                createSortOrders()
        );
    }

    @Test
    void handle_returnedPageTranslatedCorrectlyToOutput() {
        // Arrange
        MyPage<UserModel> page = createPage();
        GetAllPublicProfilesCommand dummy = mock(GetAllPublicProfilesCommand.class);

        when(userRepository.findPage(any(MyPageable.class))).thenReturn(page);

        // Act
        GetAllPublicProfilesOutput output = getAllPublicProfilesUseCase.handle(dummy);

        // Assert
        assertEquals(page.getTotalElementsAll(), output.getTotalElements());
        assertEquals(page.getTotalPages(), output.getTotalPages());
        assertThat(page.getPageRequest())
                .hasFieldOrPropertyWithValue("pageNumber", output.getCommand().getPageNumber())
                .hasFieldOrPropertyWithValue("pageSize", output.getCommand().getPageSize())
                .hasFieldOrPropertyWithValue("sortOrders", output.getCommand().getMySortOrders());

        verify(userRepository).findPage(any(MyPageable.class));
    }


    private MyPage<UserModel> createPage() {
        return new MyPage<>(
                0,
                0,
                createPageRequest(),
                createItems(10)
        );
    }

    private List<UserModel> createItems(int n) {
        List<UserModel> items = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            UserModel userModel = new UserModel.Builder()
                    .setId((long) i)
                    .setUsername("username" + i)
                    .setEmail("email" + i)
                    .setEmailVerified(true)
                    .setActive(true)
                    .build();

            items.add(userModel);
        }

        return items;
    }

    private MyPageable createPageRequest() {
        return new MyPageable(
                10,
                90,
                createSortOrders()
        );
    }

    private List<MySortOrder> createSortOrders() {
        MySortOrder so1 = new MySortOrder("username", MySortOrder.MyDirection.ASC);
        MySortOrder so2 = new MySortOrder("email", MySortOrder.MyDirection.DESC);
        MySortOrder so3 = new MySortOrder("createdAt", MySortOrder.MyDirection.ASC);
        return List.of(so1, so2, so3);
    }
}
