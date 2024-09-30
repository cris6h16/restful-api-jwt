package org.cris6h16.Config.SpringBoot.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PasswordEncoderImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderImpl passwordEncoderImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void encode(){
        String raw = "12345678";
        String encoded = "encodedPassword";
        when(passwordEncoder.encode(raw)).thenReturn(encoded);

        String res = passwordEncoderImpl.encode(raw);

        assertEquals(encoded,res);
        verify(passwordEncoder).encode(raw);
    }

    @Test
    void matches(){
        String raw = "12345678";
        String encoded = "encodedPassword";

        when(passwordEncoder.matches(raw, encoded)).thenReturn(true);

        boolean matches = passwordEncoderImpl.matches(raw, encoded);

        assertTrue(matches);
        verify(passwordEncoder).matches(raw, encoded);
    }
}