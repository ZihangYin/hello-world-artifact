package com.unicorn.rest.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.unicorn.rest.repository.AuthenticationTokenRepository;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.AuthenticationToken;


public class AuthenticationTokenRepositoryImpl implements AuthenticationTokenRepository {

    private final Map<String, AuthenticationToken> authenticationTokens = new HashMap<>();

    @Override
    public @Nullable AuthenticationToken findToken(@Nullable String token)
            throws RepositoryClientException, RepositoryServerException {
        return authenticationTokens.get(token);
    }

    @Override
    public void persistToken(@Nullable AuthenticationToken authenticationToken)
            throws RepositoryClientException, RepositoryServerException {
        String token = authenticationToken.getToken();
        if (authenticationTokens.containsKey(token)) {
            throw new RepositoryClientException("RepositoryClientException");
        }
        authenticationTokens.put(token, authenticationToken);
    }

    @Override
    public void revokeToken(@Nullable String tokenType, @Nullable String token) throws RepositoryClientException, RepositoryServerException {
    }

    @Override
    public void authenticate(@Nullable String token, @Nullable String clientSecretProof) {
        // TODO Auto-generated method stub
    }

    @Override
    public void authorize(@Nullable String token, @Nullable String clientSecretProof,
            @Nullable List<String> scope) {
        // TODO Auto-generated method stub
    }
}
