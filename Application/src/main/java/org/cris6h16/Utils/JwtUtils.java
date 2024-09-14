package org.cris6h16.Utils;

import java.util.Map;

public interface JwtUtils {

    public String genToken(Long subject, Map<String, String> claims, long timeExpirationMillis);

    boolean validate(String token);

    Long getId(String token);
}