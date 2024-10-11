package org.cris6h16.Config.SpringBoot.Utils;

import io.jsonwebtoken.Claims;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtUtilsImplTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtilsImpl jwtUtilsImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(jwtProperties.getSecretKey()).thenReturn("secretKey123456789098763e4rfgbnmki876543wsx09876543456789nmju7654redcvbnm");
        when(jwtProperties.getToken()).thenReturn(mock(JwtProperties.Token.class));
        when(jwtProperties.getToken().getRefresh()).thenReturn(mock(JwtProperties.Token.Refresh.class));
        when(jwtProperties.getToken().getRefresh().getExpiration()).thenReturn(mock(JwtProperties.Token.Refresh.Expiration.class));
        when(jwtProperties.getToken().getRefresh().getExpiration().getSecs()).thenReturn(1000L);
        when(jwtProperties.getToken().getAccess()).thenReturn(mock(JwtProperties.Token.Access.class));
        when(jwtProperties.getToken().getAccess().getExpiration()).thenReturn(mock(JwtProperties.Token.Access.Expiration.class));
        when(jwtProperties.getToken().getAccess().getExpiration().getSecs()).thenReturn(1000L);
    }

    @Test
    void genToken_claimsNullSuccessful() {
        String token = jwtUtilsImpl.genToken(1L, null, 1000 * 60);

        Claims claims = jwtUtilsImpl.getClaims(token);

        assertThat(claims.keySet()).containsAll(List.of("sub", "iat", "exp"));
        assertEquals(3, claims.size());
        assertEquals("1", claims.get("sub"));
    }

    @Test
    void genToken_withClaimsSuccessful() {
        String token = jwtUtilsImpl.genToken(91L, Map.of("github", "github.com/cris6h16", "language", "java"), 1000 * 60);

        Claims claims = jwtUtilsImpl.getClaims(token);

        assertThat(claims.keySet()).containsAll(List.of("sub", "iat", "exp", "github", "language"));
        assertEquals(5, claims.size());
        assertEquals("91", claims.get("sub"));
        assertEquals("github.com/cris6h16", claims.get("github"));
        assertEquals("java", claims.get("language"));
    }

    @Test
    void validate_invalid() {
        boolean valid = jwtUtilsImpl.validate("invalidToken");
        assertFalse(valid);
    }

    @Test
    void validate_invalid_expired() {
        boolean valid = jwtUtilsImpl.validate(jwtUtilsImpl.genToken(44L, null, 0));
        assertFalse(valid);
    }

    @Test
    void validate_valid() {
        boolean valid = jwtUtilsImpl.validate(jwtUtilsImpl.genToken(99L, null, 999999));
        assertTrue(valid);
    }

    @Test
    void getId_successful() {
        Long id = 150L;
        String token = jwtUtilsImpl.genToken(id, null, 999999);
        Long extracted = jwtUtilsImpl.getId(token);

        assertEquals(id, extracted);
    }

    @Test
    void genRefreshToken_successful() {
        Long id = 123L;
        String refreshToken = jwtUtilsImpl.genRefreshToken(id);

        Claims claims = jwtUtilsImpl.getClaims(refreshToken);
        assertEquals("123", claims.get("sub"));
        assertThat(claims.keySet()).containsAll(List.of("sub", "iat", "exp"));
    }

    @Test
    void genAccessToken_successful() {
        Long id = 123L;
        Set<ERoles> roles = Set.of(ERoles.ROLE_ADMIN, ERoles.ROLE_USER);
        String accessToken = jwtUtilsImpl.genAccessToken(id, roles);

        Claims claims = jwtUtilsImpl.getClaims(accessToken);
        assertEquals("123", claims.get("sub"));
        assertEquals(roles.toString(), claims.get("roles"));
        assertThat(claims.keySet()).containsAll(List.of("sub", "iat", "exp", "roles"));
    }

    @ParameterizedTest
    @MethodSource("provideRoles")
    void getRoles(Set<ERoles> roles) {
        Long id = 123L;
        String accessToken = jwtUtilsImpl.genAccessToken(id, roles);

        Set<ERoles> extractedRoles = jwtUtilsImpl.getRoles(accessToken);
        assertEquals(roles, extractedRoles);
    }

    private static Stream<Set<ERoles>> provideRoles() {
        return Stream.of(
                Set.of(),
                Set.of(ERoles.ROLE_USER),
                Set.of(ERoles.ROLE_USER, ERoles.ROLE_ADMIN)
        );
    }


}