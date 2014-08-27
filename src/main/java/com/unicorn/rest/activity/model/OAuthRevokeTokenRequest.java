package com.unicorn.rest.activity.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.collections4.CollectionUtils;

import com.unicorn.rest.activities.exception.OAuthBadRequestException;
import com.unicorn.rest.activities.utils.OAuthRequestValidator;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrCode;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrDescFormatter;

@EqualsAndHashCode
@ToString
public class OAuthRevokeTokenRequest {

    public static final String OAUTH_TOKEN_TYPE = "token_type";
    public static final String OAUTH_TOKEN = "token";
 
    @Getter @Nonnull private final String tokenType;
    @Getter @Nonnull private final String token;

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
}
