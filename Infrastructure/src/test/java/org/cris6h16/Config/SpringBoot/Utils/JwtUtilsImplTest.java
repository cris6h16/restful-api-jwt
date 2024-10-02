package org.cris6h16.Config.SpringBoot.Utils;

import io.jsonwebtoken.Claims;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Models.ERoles;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
public
class JwtUtilsImplTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtUtilsImpl jwtUtilsImpl;

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

}