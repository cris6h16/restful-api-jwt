package org.cris6h16.Config.SpringBoot.Services;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.cris6h16.In.Commands.GetAllPublicProfilesCommand;
import org.cris6h16.In.Results.GetAllPublicProfilesOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class CacheServiceTest {

    @Mock
    private RedisTemplate<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> redisTemplate;

    @Mock
    private ValueOperations<GetAllPublicProfilesCommand, GetAllPublicProfilesOutput> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testGetAllUsers_Success() {
        // Arrange
        GetAllPublicProfilesCommand command = mock(GetAllPublicProfilesCommand.class);
        GetAllPublicProfilesOutput expectedOutput = mock(GetAllPublicProfilesOutput.class);

        when(valueOperations.get(command)).thenReturn(expectedOutput);

        // Act
        GetAllPublicProfilesOutput actualOutput = cacheService.getAllUsers(command);

        // Assert
        assertEquals(expectedOutput, actualOutput);
        verify(valueOperations).get(command);
    }

    @Test
    public void testGetAllUsers_NullResponse() {
        // Arrange
        GetAllPublicProfilesCommand command = mock(GetAllPublicProfilesCommand.class);

        when(valueOperations.get(command)).thenReturn(null);

        // Act
        GetAllPublicProfilesOutput actualOutput = cacheService.getAllUsers(command);

        // Assert
        assertNull(actualOutput);
        verify(valueOperations).get(command);
    }

    @Test
    public void testPutAllUsers_Success() {
        // Arrange
        GetAllPublicProfilesCommand command = mock(GetAllPublicProfilesCommand.class);
        GetAllPublicProfilesOutput output = mock(GetAllPublicProfilesOutput.class);

        // Act
        cacheService.putAllUsers(command, output);

        // Assert
        verify(valueOperations).set(command, output);
    }
}
