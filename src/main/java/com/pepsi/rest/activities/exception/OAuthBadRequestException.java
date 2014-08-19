package com.pepsi.rest.activities.exception;

import com.pepsi.rest.activity.model.OAuthErrorResponse;

public class OAuthBadRequestException extends BadRequestException {

    private static final long serialVersionUID = 204677130416245925L;
    
    public OAuthBadRequestException(OAuthErrorResponse oauthErrorResponse) {
        super(oauthErrorResponse);
    }

    public OAuthErrorResponse getOauthErrorResponse() {
        return (OAuthErrorResponse) super.getErrorResponse();
    }
}
