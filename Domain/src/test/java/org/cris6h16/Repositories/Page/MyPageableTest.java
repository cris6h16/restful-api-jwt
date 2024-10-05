package org.cris6h16.Repositories.Page;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class MyPageableTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        int expectedPageNumber = 1;
        int expectedPageSize = 10;
        List<MySortOrder> expectedSortOrders = mock(List.class);

        // Act
        MyPageable myPageable = new MyPageable(expectedPageNumber, expectedPageSize, expectedSortOrders);

        // Assert
        assertEquals(expectedPageNumber, myPageable.getPageNumber());
        assertEquals(expectedPageSize, myPageable.getPageSize());
        assertEquals(expectedSortOrders, myPageable.getSortOrders());
    }

    @Test
    void testDefaultConstructorWithEmptySortOrders() {
        // Arrange
        int expectedPageNumber = 0;
        int expectedPageSize = 5;
        List<MySortOrder> expectedSortOrders = Collections.emptyList();

        // Act
        MyPageable myPageable = new MyPageable(expectedPageNumber, expectedPageSize, expectedSortOrders);

        // Assert
        assertEquals(expectedPageNumber, myPageable.getPageNumber());
        assertEquals(expectedPageSize, myPageable.getPageSize());
        assertTrue(myPageable.getSortOrders().isEmpty());
    }
}
