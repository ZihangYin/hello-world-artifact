package com.unicorn.rest.server.filter.utils;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.unicorn.rest.activities.exception.AccessDeniedException;
import com.unicorn.rest.activities.exception.BadRequestException;
import com.unicorn.rest.activities.exception.MissingAuthorizationException;
import com.unicorn.rest.activities.exception.UnrecognizedAuthorizationMethodException;
import com.unicorn.rest.repository.AuthorizationRepository;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;

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

    protected final String[] authenticationCode;
    
    public static @Nonnull Authorizer validateAuthorizationHeader(@Nullable String authorizationHeader) 
            throws BadRequestException {
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new MissingAuthorizationException();
        }
        return getAuthorizer(authorizationHeader);
    }

    public static @Nonnull Authorizer validateAuthorizationHeader(@Nullable String authorizationHeader, @Nonnull AuthenticationMethod expectedAuthMethod) 
            throws BadRequestException {
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new MissingAuthorizationException();
        }
        if (authorizationHeader.startsWith(expectedAuthMethod.toString())) {
            return getAuthorizerForAuthMethod(authorizationHeader, expectedAuthMethod);
        }
        throw new UnrecognizedAuthorizationMethodException();
    }

    private static @Nonnull Authorizer getAuthorizer(@Nonnull String authorizationHeader) throws BadRequestException {
        if (authorizationHeader.startsWith(AuthenticationMethod.BASIC_AUTHENTICATION.toString())) {
            return new BasicAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BASIC_AUTHENTICATION.toString(), ""));
        } else if (authorizationHeader.startsWith(AuthenticationMethod.BEARER_AUTHENTICATION.toString())) {
            return new BearerAuthorizer(authorizationHeader.replaceFirst(AuthenticationMethod.BEARER_AUTHENTICATION.toString(), ""));
        } else {
            throw new UnrecognizedAuthorizationMethodException();
        }
    }
    
    private static @Nonnull Authorizer getAuthorizerForAuthMethod(@Nonnull String authorizationHeader, @Nonnull AuthenticationMethod authenticationMethod) 
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

    protected Authorizer(@Nonnull String authorizationCode) throws AccessDeniedException {
        this.authenticationCode = parseAuthorizationCode(authorizationCode);
    }

    protected abstract @Nonnull String[] parseAuthorizationCode (@Nonnull String authorizationCode) throws AccessDeniedException;
    
    /**
     * 
     * @param authorizationRepository
     * @throws RepositoryClientException
     * @throws RepositoryServerException
     */
    public abstract void authenticate(@Nullable AuthorizationRepository authorizationRepository) throws RepositoryClientException, RepositoryServerException;
    /**
     * 
     * @param authorizationRepository
     * @param scope
     * @throws RepositoryClientException
     * @throws RepositoryServerException
     */
    public abstract void authorize(@Nullable AuthorizationRepository authorizationRepository, @Nullable List<String> scope) throws RepositoryClientException, RepositoryServerException;
}
