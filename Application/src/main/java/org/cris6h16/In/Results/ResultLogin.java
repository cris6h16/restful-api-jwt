package org.cris6h16.In.Results;

public class ResultLogin {
    private final String accessToken;
    private final String refreshToken;

    public ResultLogin(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
