package com.pepsi.rest.oauth.provider.model;

public enum GrantType {
    // NONE("none"),
    AUTHORIZATION_CODE("authorization_code"),
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token");

    private String grantType;

    GrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public String toString() {
        return grantType;
    }
}