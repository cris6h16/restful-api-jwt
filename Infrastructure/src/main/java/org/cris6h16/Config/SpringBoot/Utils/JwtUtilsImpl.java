package org.cris6h16.Config.SpringBoot.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.cris6h16.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtilsImpl implements JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Override
    public String genToken(String subject, Map<String, String> claims, long timeExpirationMillis) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(subject)
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


    public boolean validate(String token) {
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

    public <T> T getAClaim(String token, Function<Claims, T> individualClaim) {
        Claims claims = getClaims(token);
        return individualClaim.apply(claims);
    }

    public String getUsername(String token) {
        return getAClaim(token, Claims::getSubject);
    }


    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSign())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public SecretKey getSign() {
        byte[] keyBase = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBase);
    }

}