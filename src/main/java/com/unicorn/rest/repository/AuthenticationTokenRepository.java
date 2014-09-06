package com.unicorn.rest.repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.AuthenticationToken;
import com.unicorn.rest.repository.model.AuthenticationToken.AuthenticationTokenType;

public interface AuthenticationTokenRepository {

    public @Nonnull AuthenticationToken findToken(@Nullable AuthenticationTokenType tokenType, @Nullable String token) throws RepositoryClientException, RepositoryServerException;
    public void persistToken(@Nullable AuthenticationToken authenticationToken) throws RepositoryClientException, RepositoryServerException;
    public void revokeToken(@Nullable AuthenticationTokenType tokenType, @Nullable String token) throws RepositoryClientException, RepositoryServerException;
}
