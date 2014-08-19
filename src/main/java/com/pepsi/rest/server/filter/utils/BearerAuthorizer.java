package com.pepsi.rest.server.filter.utils;

import javax.annotation.Nonnull;

import com.pepsi.rest.activities.exception.BadRequestException;

public class BearerAuthorizer extends Authorizer {
    
    public BearerAuthorizer(@Nonnull String authorizationCode) throws BadRequestException {
        super(authorizationCode);
    }

    @Override
    public void authenticate() {
    }
    
    @Override
    public void authorize() {
    }
}
