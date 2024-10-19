package org.cris6h16.In.Results;

import static org.junit.jupiter.api.Assertions.*;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.Repositories.Page.MySortOrder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.List;

public class GetAllPublicProfilesOutputTest {

    @Test
    public void noArgsConstructor() {
        // Act
        GetAllPublicProfilesOutput output = new GetAllPublicProfilesOutput();

        // Assert
        assertNull(output.getCommand());
        assertNull(output.getItems());
        assertEquals(0, output.getTotalElements());
        assertEquals(0, output.getTotalPages());
    }


    @Test
    public void testConstructorAndGetters() {
        // Arrange
        long totalElements = 100L;
        long totalPages = 10L;
        GetAllPublicProfilesCommand command = mock(GetAllPublicProfilesCommand.class);
        List<GetPublicProfileOutput> items = mock(List.class);

        // Act
        GetAllPublicProfilesOutput output = new GetAllPublicProfilesOutput(totalElements, totalPages, command, items);

        // Assert
        assertEquals(totalElements, output.getTotalElements());
        assertEquals(totalPages, output.getTotalPages());
        assertEquals(command, output.getCommand());
        assertEquals(items, output.getItems());
    }

    @Test
    public void testConstructorWithEmptyItems() {
        // Arrange
        long totalElements = 50L;
        long totalPages = 5L;
        GetAllPublicProfilesCommand command = new GetAllPublicProfilesCommand(2, 20, List.of());
        List<GetPublicProfileOutput> emptyItems = List.of();

        // Act
        GetAllPublicProfilesOutput output = new GetAllPublicProfilesOutput(totalElements, totalPages, command, emptyItems);

        // Assert
        assertEquals(totalElements, output.getTotalElements());
        assertEquals(totalPages, output.getTotalPages());
        assertEquals(command, output.getCommand());
        assertTrue(output.getItems().isEmpty());
    }
}
