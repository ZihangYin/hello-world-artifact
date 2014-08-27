package com.unicorn.rest.activity.model;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unicorn.rest.activities.exception.OAuthBadRequestException;
import com.unicorn.rest.activities.utils.OAuthRequestValidator;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrCode;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrDescFormatter;

@EqualsAndHashCode
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

    @Getter @Nonnull private final GrantType grantType;
    private final String clientId;
    private final String requestToken;
    private final String redirectUri;
    private final String userName;
    private final String password;
    private final String refreshToken;
    private final List<String> scope;

    public static OAuthTokenRequest validateRequestFromMultiValuedParameters(@Nullable MultivaluedMap<String, String> multiValuedParameters) throws OAuthBadRequestException {
        if (CollectionUtils.sizeIsEmpty(multiValuedParameters)) {
            throw new OAuthBadRequestException(OAuthErrCode.INVALID_REQUEST, OAuthErrDescFormatter.INVALID_REQUEST.toString());
        }

        return new OAuthTokenRequest(multiValuedParameters.getFirst(OAUTH_GRANT_TYPE), multiValuedParameters.getFirst(OAUTH_CLIENT_ID), 
                multiValuedParameters.getFirst(OAUTH_REQUEST_TOKEN), multiValuedParameters.getFirst(OAUTH_REDIRECT_URI), 
                multiValuedParameters.getFirst(OAUTH_USER_NAME), multiValuedParameters.getFirst(OAUTH_PASSWORD), 
                multiValuedParameters.getFirst(OAUTH_REFRESH_TOKEN), multiValuedParameters.getFirst(OAUTH_SCOPE));
    }

    private OAuthTokenRequest(String grantType, String clientId,
            String requestToken, String redirectUri, String userName, 
            String password, String refreshToken, String scope) throws OAuthBadRequestException {

        OAuthRequestValidator.validateRequiredParameter(OAUTH_GRANT_TYPE, grantType); 

        if (GrantType.PASSWORD.toString().equals(grantType)) {
            OAuthRequestValidator.validateRequiredParameter(OAUTH_USER_NAME, userName);
            OAuthRequestValidator.validateRequiredParameter(OAUTH_PASSWORD, password);
            this.grantType = GrantType.PASSWORD;
        } else if (GrantType.CLIENT_CREDENTIAL.toString().equals(grantType)) {
            OAuthRequestValidator.validateRequiredParameter(OAUTH_CLIENT_ID, clientId);
            this.grantType = GrantType.CLIENT_CREDENTIAL;
        } else if (GrantType.AUTHORIZATION_CODE.toString().equals(grantType)) {
            OAuthRequestValidator.validateRequiredParameter(OAUTH_REQUEST_TOKEN, requestToken);
            OAuthRequestValidator.validateRequiredParameter(OAUTH_CLIENT_ID, clientId);
            OAuthRequestValidator.validateRequiredParameter(OAUTH_REDIRECT_URI, redirectUri);
            this.grantType = GrantType.AUTHORIZATION_CODE;
        } else {
            throw new OAuthBadRequestException(OAuthErrCode.UNSUPPORTED_GRANT_TYPE,  
                    String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), grantType));
        }

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
    
    /**
     * @return clientId @Nonnull if this is required parameter for this grant type
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return requestToken @Nonnull if this is required parameter for this grant type
     */
    public String getRequestToken() {
        return requestToken;
    }
    /**
     * @return redirectUri @Nonnull if this is required parameter for this grant type
     */
    public String getRedirectUri() {
        return redirectUri;
    }
    /**
     * @return userName @Nonnull if this is required parameter for this grant type
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return password @Nonnull if this is required parameter for this grant type
     */
    public String getPassword() {
        return password;
    }
    /**
     * @return refreshToken @Nonnull if this is required parameter for this grant type
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    /**
     * @return scope @Nonnull if this is required parameter for this grant type
     */
    public List<String> getScope() {
        return scope;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("OAuthTokenRequest [grantType=").append(grantType);
        if (clientId != null) {
            builder.append(", clientId=").append(clientId);
        }
        if (requestToken != null) {
            builder.append(", requestToken=").append(requestToken);
        }
        if (redirectUri != null) {
            builder.append(", redirectUri=").append(redirectUri);
        }
        if (userName != null) {
            builder.append(", userName=").append(userName);
        }
        if (password != null) {
            builder.append(", password=").append(password);
        }
        if (refreshToken != null) {
            builder.append(", refreshToken=").append(refreshToken);
        }
        if (scope != null) {
            builder.append(", scope=").append(scope);
        }
        builder.append("]");
        return builder.toString();
    }
}