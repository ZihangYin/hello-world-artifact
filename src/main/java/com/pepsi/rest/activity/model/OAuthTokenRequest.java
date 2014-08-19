package com.pepsi.rest.activity.model;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;

import com.pepsi.rest.activities.exception.OAuthBadRequestException;
import com.pepsi.rest.activities.utils.OAuthRequestValidator;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrDescFormatter;

public class OAuthTokenRequest {

    public static final String OAUTH_GRANT_TYPE = "grant_type";    
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_REQUEST_TOKEN = "request_token";
    public static final String OAUTH_REDIRECT_URI = "redirect_uri";    
    public static final String OAUTH_USER_NAME = "user_name";
    public static final String OAUTH_PASSWORD = "password";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_SCOPE = "scope";

    public enum GrantType {
        AUTHORIZATION_CODE("authorization_code"),
        PASSWORD("password"),
        CLIENT_CREDENTIAL("client_credentials"),
        REFRESH_TOKEN("refresh_token");

        private String grantType;

        private GrantType(String grantType) {
            this.grantType = grantType;
        }

        @Override
        public String toString() {
            return grantType;
        }
    }
    
    private final String grantType;
    private final String clientId;
    private final String requestToken;
    private final String redirectUri;
    private final String userName;
    private final String password;
    private final String refreshToken;
    private final List<String> scope;

    public static OAuthTokenRequest fromMultiValuedParameters(@Nullable MultivaluedMap<String, String> multiValuedParameters) throws OAuthBadRequestException {
        if(multiValuedParameters == null || multiValuedParameters.isEmpty()) {
            throw new OAuthBadRequestException(new OAuthErrorResponse(OAuthErrCode.INVALID_REQUEST, OAuthErrDescFormatter.INVALID_REQUEST.toString()));
        }
        
        return new OAuthTokenRequest(multiValuedParameters.getFirst(OAUTH_GRANT_TYPE), multiValuedParameters.getFirst(OAUTH_CLIENT_ID), 
                multiValuedParameters.getFirst(OAUTH_REQUEST_TOKEN), multiValuedParameters.getFirst(OAUTH_REDIRECT_URI), 
                multiValuedParameters.getFirst(OAUTH_USER_NAME), multiValuedParameters.getFirst(OAUTH_PASSWORD), 
                multiValuedParameters.getFirst(OAUTH_REFRESH_TOKEN), multiValuedParameters.getFirst(OAUTH_SCOPE));
    }

    public OAuthTokenRequest(String grantType, String clientId,
            String requestToken, String redirectUri, String userName, 
            String password, String refreshToken, String scope) throws OAuthBadRequestException {
        OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_GRANT_TYPE, grantType); 
        this.grantType = grantType;
        this.clientId = clientId;
        this.requestToken = requestToken;
        this.redirectUri = redirectUri;
        this.userName = userName;
        this.password = password;
        this.refreshToken = refreshToken;
        if (!StringUtils.isBlank(scope)) {
            // TODO: Better validation of scope fields
            this.scope = Arrays.asList(scope.split(","));
        } else {
            this.scope = null;
        }
    }

    public @Nonnull String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public List<String> getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "OAuthTokenRequest [grantType=" + grantType + ", clientId="
                + clientId + ", requestToken=" + requestToken + ", redirectUri="
                + redirectUri + ", userName=" + userName + ", password="
                + password + ", refreshToken=" + refreshToken + ", scope="
                + scope + "]";
    }
}