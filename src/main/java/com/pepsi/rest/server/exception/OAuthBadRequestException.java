package com.pepsi.rest.server.exception;

import com.pepsi.rest.oauth.provider.model.OAuthErrorResponse;

public class OAuthBadRequestException extends Exception {

    private static final long serialVersionUID = 204677130416245925L;
    private final OAuthErrorResponse oauthErrorResponse;
    
    public OAuthBadRequestException(OAuthErrorResponse oauthErrorResponse) {
        super();
        this.oauthErrorResponse = oauthErrorResponse;
    }

    public OAuthErrorResponse getOauthErrorResponse() {
        return oauthErrorResponse;
    }
}
