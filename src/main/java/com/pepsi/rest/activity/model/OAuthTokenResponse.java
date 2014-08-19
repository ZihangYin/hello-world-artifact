package com.pepsi.rest.activity.model;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pepsi.rest.repository.model.AuthenticationToken;

@XmlRootElement(name="token")
@JsonInclude(value=Include.NON_NULL)
public class OAuthTokenResponse {
    //Authorization response parameters
    private static final String OAUTH_TOKEN_TYPE = "token_type";
    private static final String OAUTH_ACCESS_TOKEN = "access_token";
    private static final String OAUTH_EXPIRES_AT = "expire_at";
    private static final String OAUTH_REFRESH_TOKEN = "refresh_token";

    @JsonProperty(OAUTH_TOKEN_TYPE)
    private String tokenType;
    @JsonProperty(OAUTH_ACCESS_TOKEN)
    private String accessToken;
    @JsonProperty(OAUTH_EXPIRES_AT)
    @JsonInclude(value=Include.NON_DEFAULT)
    private Date expireAt;
    @JsonProperty(OAUTH_REFRESH_TOKEN)
    private String refreshToken;
    
    public OAuthTokenResponse() {};

    public OAuthTokenResponse(@Nonnull AuthenticationToken authenticationToken) {
        this.tokenType = authenticationToken.getTokenType().toString();
        this.accessToken = authenticationToken.getToken();
        this.expireAt = authenticationToken.getExpireAt().toDate();
        this.refreshToken = authenticationToken.getRefreshToken();
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "OAuthTokenResponse [tokenType=" + tokenType + ", accessToken="
                + accessToken + ", expireAt=" + expireAt + ", refreshToken="
                + refreshToken + "]";
    }
}
