package org.cris6h16.Utils;

import org.cris6h16.Models.ERoles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface JwtUtils {

    String genRefreshToken(Long id);

    String genAccessToken(Long id, Set<ERoles> roles);

}