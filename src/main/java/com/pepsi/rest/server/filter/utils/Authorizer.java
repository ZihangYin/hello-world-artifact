package com.pepsi.rest.server.filter.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;

import com.pepsi.rest.activities.exception.AccessDeniedException;
import com.pepsi.rest.activities.exception.BadRequestException;
import com.pepsi.rest.activities.exception.MissingAuthorizationException;
import com.pepsi.rest.activities.exception.UnrecognizedAuthorizationMethodException;

public abstract class Authorizer {

    public enum AuthenticationMethod {
        BASIC_AUTHENTICATION("Basic "),
        BEARER_AUTHENTICATION("Bearer ");

        private String authMethod;

        private AuthenticationMethod(String authMethod) {
            this.authMethod = authMethod;
        }

        @Override
        public String toString() {
            return this.authMethod;
        }
    }

    protected final String principal;
    protected final String credential;

    public static Authorizer validateAuthorizationHeader(@Nullable String authorizationHeader) 
            throws BadRequestException {
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new MissingAuthorizationException();
        }
        return getAuthorizer(authorizationHeader);
    }

    public static Authorizer validateAuthorizationHeader(@Nullable String authorizationHeader, @Nonnull AuthenticationMethod expectedAuthMethod) 
            throws BadRequestException {
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new MissingAuthorizationException();
        }
        if (authorizationHeader.startsWith(expectedAuthMethod.toString())) {
            return getAuthorizerForAuthMethod(authorizationHeader, expectedAuthMethod);
        }
        throw new UnrecognizedAuthorizationMethodException();
    }

    private static Authorizer getAuthorizer(@Nonnull String authorizationHeader) throws BadRequestException {
        if (authorizationHeader.startsWith(AuthenticationMethod.BASIC_AUTHENTICATION.toString())) {
            return new BasicAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BASIC_AUTHENTICATION.toString(), ""));
        } else if (authorizationHeader.startsWith(AuthenticationMethod.BEARER_AUTHENTICATION.toString())) {
            return new BearerAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BEARER_AUTHENTICATION.toString(), ""));
        } else {
            throw new UnrecognizedAuthorizationMethodException();
        }
    }
    
    private static Authorizer getAuthorizerForAuthMethod(@Nonnull String authorizationHeader, @Nonnull AuthenticationMethod authenticationMethod) 
            throws BadRequestException {
        switch(authenticationMethod) {
        case BASIC_AUTHENTICATION:
            return new BasicAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BASIC_AUTHENTICATION.toString(), ""));
        case BEARER_AUTHENTICATION: 
            return new BearerAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BEARER_AUTHENTICATION.toString(), ""));
        default:
            throw new UnrecognizedAuthorizationMethodException();
        }
    }

    protected Authorizer(@Nonnull String authorizationCode) throws BadRequestException {
        String[] authentication = parseAuthorizationCode(authorizationCode);
        this.principal = authentication[0];
        this.credential = authentication[1];
    }

    protected String[] parseAuthorizationCode (@Nonnull String authorizationCode) throws AccessDeniedException {
        //Decode the Base64 into byte[]
        String[] authentication = Base64.decodeAsString(authorizationCode).split(":");
        if (authentication.length != 2 || StringUtils.isBlank(authentication[0]) || StringUtils.isBlank(authentication[1])) {
            throw new AccessDeniedException();
        }
        return authentication;
    }

    public abstract void authenticate() throws BadRequestException;
    public abstract void authorize();
}
