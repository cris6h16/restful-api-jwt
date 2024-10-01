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
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component("JwtUtils")
@Getter
@Slf4j
public class JwtUtilsImpl implements JwtUtils {

    private final JwtProperties jwtProperties;

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
        return Long.valueOf(getAClaim(token, claims -> claims.getSubject()));
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
        return genToken(id, Map.of("roles", roles.toString()), this.jwtProperties.getToken().getAccess().getExpiration().getSecs());
    }
}


