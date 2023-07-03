package com.nn.jwt_java.model;

public class AuthResponse {

    private String access_token;
    private int expires_in;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getToken_type() {
        return token_type;
    }
}
