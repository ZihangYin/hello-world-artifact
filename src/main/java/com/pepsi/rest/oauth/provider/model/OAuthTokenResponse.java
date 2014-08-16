package com.pepsi.rest.oauth.provider.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name="token")
public class OAuthTokenResponse {
    //Authorization response parameters
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_IN_SECONDS = "expire_in_seconds";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_TOKEN_TYPE = "token_type";

    @JsonProperty(OAUTH_ACCESS_TOKEN)
    private String accessToken;
    @JsonProperty(OAUTH_EXPIRES_IN_SECONDS)
    @JsonInclude(value=Include.NON_DEFAULT)
    private long expireInSeconds;

    private OAuthTokenResponse() {};

    private OAuthTokenResponse(String accessToken, long expireInSeconds) {
        this.accessToken = accessToken;
        this.expireInSeconds = expireInSeconds;
    }

    public static OAuthTokenResponseBuilder accessToken(String accessToken) {
        return new OAuthTokenResponseBuilder(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpireInSeconds() {
        return expireInSeconds;
    }

    public void setExpireInSeconds(long expireInSeconds) {
        this.expireInSeconds = expireInSeconds;
    }

    @Override
    public String toString() {
        return "OAuthTokenResponse [accessToken=" + accessToken
                + ", expireInSeconds=" + expireInSeconds + "]";
    }

    public static class OAuthTokenResponseBuilder {

        private String accessToken;
        private long expireInSeconds;

        public OAuthTokenResponseBuilder(String accessToken) {
            this.accessToken = accessToken;
        }

        public OAuthTokenResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public OAuthTokenResponseBuilder expireInSeconds(long expireInSeconds) {
            this.expireInSeconds = expireInSeconds;
            return this;
        }

        public OAuthTokenResponse build() {
            return new OAuthTokenResponse(accessToken, expireInSeconds);
        }
    }
}
