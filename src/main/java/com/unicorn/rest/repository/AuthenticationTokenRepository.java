package com.unicorn.rest.repository;

import javax.annotation.Nullable;
import javax.inject.Singleton;

import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.AuthenticationToken;

@Singleton
public interface AuthenticationTokenRepository extends BearerAuthorizationRepository {

    public @Nullable AuthenticationToken findToken(@Nullable String token) throws RepositoryClientException, RepositoryServerException;
    public void persistToken(@Nullable AuthenticationToken authenticationToken) throws RepositoryClientException, RepositoryServerException;
    public void revokeToken(@Nullable String tokenType, @Nullable String token) throws RepositoryClientException, RepositoryServerException;
}
