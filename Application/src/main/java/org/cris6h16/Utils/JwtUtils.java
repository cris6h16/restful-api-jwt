package org.cris6h16.Utils;

import java.util.Map;

public interface JwtUtils {

    public String genToken(String subject, Map<String, String> claims, long timeExpirationMillis);

}