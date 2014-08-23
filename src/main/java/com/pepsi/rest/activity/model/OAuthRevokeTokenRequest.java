package com.pepsi.rest.activity.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.collections4.CollectionUtils;

import com.pepsi.rest.activities.exception.OAuthBadRequestException;
import com.pepsi.rest.activities.utils.OAuthRequestValidator;
import com.pepsi.rest.activity.model.OAuthErrors.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrors.OAuthErrDescFormatter;

public class OAuthRevokeTokenRequest {

    public static final String OAUTH_TOKEN_TYPE = "token_type";
    public static final String OAUTH_TOKEN = "token";
 
    private final String tokenType;
    private final String token;

    public static OAuthRevokeTokenRequest validateRequestFromMultiValuedParameters(@Nullable MultivaluedMap<String, String> multiValuedParameters) 
            throws OAuthBadRequestException {
        if (CollectionUtils.sizeIsEmpty(multiValuedParameters)) {
            throw new OAuthBadRequestException(OAuthErrCode.INVALID_REQUEST, OAuthErrDescFormatter.INVALID_REQUEST.toString());
        }
        return new OAuthRevokeTokenRequest(multiValuedParameters.getFirst(OAUTH_TOKEN_TYPE), multiValuedParameters.getFirst(OAUTH_TOKEN));
    }

    private OAuthRevokeTokenRequest(@Nullable String tokenType, @Nullable String token) throws OAuthBadRequestException {
        
        this.tokenType = OAuthRequestValidator.validateRequiredParameter(OAUTH_TOKEN_TYPE, tokenType); 
        this.token = OAuthRequestValidator.validateRequiredParameter(OAUTH_TOKEN, token);
    }
    
    public @Nonnull String getTokenType() {
        return tokenType;
    }

    public @Nonnull String getToken() {
        return token;
    }
}
