package com.pepsi.rest.activities.exception;

import javax.annotation.Nonnull;

import com.pepsi.rest.activity.model.OAuthErrors.OAuthErrCode;

public class OAuthBadRequestException extends BadRequestException {

    private static final long serialVersionUID = 204677130416245925L;
    
    public OAuthBadRequestException(@Nonnull OAuthErrCode errorCode, @Nonnull String errorDescription) {
        super(errorCode.toString(), errorDescription);
    }
}
