package org.cris6h16.Config.SpringBoot.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.cris6h16.Models.ERoles;
import org.cris6h16.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class JwtUtilsImpl implements JwtUtils {

    private final String secretKey;
    private final long accessTokenExpTimeSecs;
    private final long refreshTokenExpTimeSecs;

    public JwtUtilsImpl(String secretKey, long accessTokenExpTimeSecs, long refreshTokenExpTimeSecs) {
        this.secretKey = secretKey;
        this.accessTokenExpTimeSecs = accessTokenExpTimeSecs;
        this.refreshTokenExpTimeSecs = refreshTokenExpTimeSecs;
    }

     public String genToken(Long subject, Map<String, String> claims, long timeExpirationMillis) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(String.valueOf(subject))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + timeExpirationMillis))
                .signWith(getSign());

        if (claims != null && !claims.isEmpty()) {
            for (Map.Entry<String, String> entry : claims.entrySet()) {
                jwtBuilder.claim(entry.getKey(), entry.getValue());
            }
        }

        return jwtBuilder.compact();
    }


   public   boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSign())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getId(String token) {
        return Long.valueOf(getAClaim(token, claims -> claims.getSubject()));
    }

     <T> T getAClaim(String token, Function<Claims, T> individualClaim) {
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
        byte[] keyBase = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBase);
    }

    @Override
    public String genRefreshToken(Long id) {
        return genToken(id, null, refreshTokenExpTimeSecs);
    }

    @Override
    public String genAccessToken(Long id, Set<ERoles> roles) {
        return genToken(id, Map.of("roles", roles.toString()), accessTokenExpTimeSecs);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public long getAccessTokenExpTimeSecs() {
        return accessTokenExpTimeSecs;
    }

    public long getRefreshTokenExpTimeSecs() {
        return refreshTokenExpTimeSecs;
    }
}