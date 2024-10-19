package org.cris6h16.In.Commands;

import org.cris6h16.Repositories.Page.MySortOrder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.List;

public class GetAllPublicProfilesCommandTest {

    @Test
    public void noArgsConstructor() {
        // Act
        GetAllPublicProfilesCommand command = new GetAllPublicProfilesCommand();

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getPageNumber());
        assertEquals(0, command.getPageSize());
        assertNull(command.getMySortOrders());
    }


    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int pageNumber = 1;
        int pageSize = 10;
        List<MySortOrder> mySortOrders = mock(List.class);

        // Act
        GetAllPublicProfilesCommand command = new GetAllPublicProfilesCommand(pageNumber, pageSize, mySortOrders);

        // Assert
        assertEquals(pageNumber, command.getPageNumber());
        assertEquals(pageSize, command.getPageSize());
        assertEquals(mySortOrders, command.getMySortOrders());
    }

    @Test
    public void testEquals_sameObject() {
        // Arrange
        GetAllPublicProfilesCommand command = new GetAllPublicProfilesCommand(1, 10, List.of());

        // Act & Assert
        assertEquals(command, command); // Same reference
    }

    @Test
    public void testEquals_differentObjectSameValues() {
        // Arrange
        GetAllPublicProfilesCommand command1 = createCommand();
        GetAllPublicProfilesCommand command2 = createCommand();

        // Act & Assert
        assertEquals(command1, command2); // Same values
    }

    private GetAllPublicProfilesCommand createCommand() {
        return new GetAllPublicProfilesCommand(
                1,
                10,
                createMySortOrders()
        );
    }

    private List<MySortOrder> createMySortOrders() {
        return List.of(
            new MySortOrder("name", MySortOrder.MyDirection.ASC),
            new MySortOrder("age", MySortOrder.MyDirection.DESC),
            new MySortOrder("email", MySortOrder.MyDirection.ASC)
        );
    }

    @Test
    public void testEquals_differentObjectDifferentValues() {
        // Arrange
        GetAllPublicProfilesCommand command1 = new GetAllPublicProfilesCommand(1, 10, List.of(new MySortOrder("name", MySortOrder.MyDirection.ASC)));
        GetAllPublicProfilesCommand command2 = new GetAllPublicProfilesCommand(2, 20, List.of(new MySortOrder("age", MySortOrder.MyDirection.DESC)));

        // Act & Assert
        assertNotEquals(command1, command2); // Different values
    }

    @Test
    public void testEquals_nullObject() {
        // Arrange
        GetAllPublicProfilesCommand command = createCommand();

        // Act & Assert
        assertFalse(command.equals(null)); // Null check
    }

    @Test
    public void testEquals_differentClass() {
        // Arrange
        GetAllPublicProfilesCommand command = createCommand();

        // Act & Assert
        assertFalse(command.equals("string")); // Different class
    }
}
