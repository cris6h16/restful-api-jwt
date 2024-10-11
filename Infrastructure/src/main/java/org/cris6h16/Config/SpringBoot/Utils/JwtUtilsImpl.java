package org.cris6h16.Config.SpringBoot.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.cris6h16.Config.SpringBoot.Properties.JwtProperties;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("JwtUtils")
@Getter
@Slf4j
public class JwtUtilsImpl implements JwtUtils {

    private final JwtProperties jwtProperties;
    private final String ROLE_CLAIM = "roles";

    public JwtUtilsImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    public String genToken(Long subject, Map<String, String> claims, long timeExpirationSecs) {
        log.debug("Generating token");

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(String.valueOf(subject))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (timeExpirationSecs * 1000)))
                .signWith(getSign());

        if (claims != null && !claims.isEmpty()) {
            for (Map.Entry<String, String> entry : claims.entrySet()) {
                jwtBuilder.claim(entry.getKey(), entry.getValue());

                log.debug("Added claim: {} = {}", entry.getKey(), entry.getValue());
            }
        }

        log.debug("Token generated");
        return jwtBuilder.compact();
    }


    public boolean validate(String token) {
        log.debug("Validating token");
        try {
            Jwts.parser()
                    .verifyWith(getSign())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            log.debug("Token is valid");
            return true;
        } catch (Exception e) {
            log.debug("Token is invalid");
            return false;
        }
    }

    public Long getId(String token) {
        return Long.valueOf(getAClaim(token, Claims::getSubject));
    }


    //  CLAIM: "roles": [ROLE_USER, ROLE_ADMIN]
    // [\\[\\]\\s] -> match any character inside the brackets [], those as special characters are escaped with \\
    // "[ROLE_USER, ROLE_ADMIN]".replaceAll("[\\[\\]\\s]", "") ==> "ROLE_USER,ROLE_ADMIN"

    /**
     * Extracts roles from token
     * @param token token to extract roles from
     * @return set of roles, if no roles found, returns empty set
     */
    public Set<ERoles> getRoles(String token) {
        String rolesStr = getAClaim(token, claims -> claims.get(ROLE_CLAIM, String.class));

        if (rolesStr == null || rolesStr.isEmpty()) {
            log.debug("No roles claim found in token");
            return Collections.emptySet();
        }

        try {
            return Arrays.stream(rolesStr.replaceAll("[\\[\\]\\s]", "").split(","))
                    .map(ERoles::valueOf)
                    .collect(Collectors.toSet());

        } catch (IllegalArgumentException e) {
            log.error("Invalid role found in token: {}", rolesStr, e);
            return Collections.emptySet();
        }
    }

    <T> T getAClaim(String token, Function<Claims, T> individualClaim) {
        log.debug("Extracting a claim from token, function: {}", individualClaim.toString());
        Claims claims = getClaims(token);
        return individualClaim.apply(claims);
    }


    Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSign())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    SecretKey getSign() {
        byte[] keyBase = Decoders.BASE64.decode(this.jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBase);
    }

    @Override
    public String genRefreshToken(Long id) {
        log.debug("Generating refresh token for user ID: {}", id);
        return genToken(id, null, this.jwtProperties.getToken().getRefresh().getExpiration().getSecs());
    }

    @Override
    public String genAccessToken(Long id, Set<ERoles> roles) {
        log.debug("Generating access token for user ID: {}", id);
        return genToken(id, Map.of(ROLE_CLAIM, roles.toString()), this.jwtProperties.getToken().getAccess().getExpiration().getSecs());
    }
}


