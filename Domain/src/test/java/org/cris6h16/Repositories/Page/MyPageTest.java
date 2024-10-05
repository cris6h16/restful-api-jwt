package org.cris6h16.Repositories.Page;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class MyPageTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        int expectedTotalPages = 5;
        long expectedTotalElements = 100;
        MyPageable expectedPageRequest = mock(MyPageable.class);
        List<String> expectedItems = mock(List.class);

        // Act
        MyPage<String> myPage = new MyPage<>(expectedTotalPages, expectedTotalElements, expectedPageRequest, expectedItems);

        // Assert
        assertEquals(expectedTotalPages, myPage.getTotalPages());
        assertEquals(expectedTotalElements, myPage.getTotalElementsAll());
        assertEquals(expectedPageRequest, myPage.getPageRequest());
        assertEquals(expectedItems, myPage.getItems());
    }

    @Test
    void testEmptyItems() {
        // Arrange
        int expectedTotalPages = 0;
        long expectedTotalElements = 0;
        MyPageable expectedPageRequest = new MyPageable(0, 10, List.of());
        List<String> expectedItems = Collections.emptyList();

        // Act
        MyPage<String> myPage = new MyPage<>(expectedTotalPages, expectedTotalElements, expectedPageRequest, expectedItems);

        // Assert
        assertEquals(expectedTotalPages, myPage.getTotalPages());
        assertEquals(expectedTotalElements, myPage.getTotalElementsAll());
        assertEquals(expectedPageRequest, myPage.getPageRequest());
        assertTrue(myPage.getItems().isEmpty());
    }

    @Test
    void testSingleItem() {
        // Arrange
        int expectedTotalPages = 1;
        long expectedTotalElements = 1;
        MyPageable expectedPageRequest = new MyPageable(0, 1, List.of());
        List<String> expectedItems = Collections.singletonList("item1");

        // Act
        MyPage<String> myPage = new MyPage<>(expectedTotalPages, expectedTotalElements, expectedPageRequest, expectedItems);

        // Assert
        assertEquals(expectedTotalPages, myPage.getTotalPages());
        assertEquals(expectedTotalElements, myPage.getTotalElementsAll());
        assertEquals(expectedPageRequest, myPage.getPageRequest());
        assertEquals(expectedItems, myPage.getItems());
    }
}
