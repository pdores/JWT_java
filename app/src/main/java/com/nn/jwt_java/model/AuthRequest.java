package com.nn.jwt_java.model;

public class AuthRequest {

    private String grant_type;
    private String client_secret;
    private String client_id;

    public AuthRequest(String grant_type, String client_secret, String client_id) {
        this.grant_type = grant_type;
        this.client_secret = client_secret;
        this.client_id = client_id;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public String getClient_id() {
        return client_id;
    }
}
