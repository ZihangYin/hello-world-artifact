package com.pepsi.rest.repository.impl;

import java.util.HashMap;
import java.util.Map;

import com.pepsi.rest.repository.AuthenticationTokenRepository;
import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryServerException;
import com.pepsi.rest.repository.model.AuthenticationToken;


public class AuthenticationTokenRepositoryImpl implements AuthenticationTokenRepository {

    private final Map<String, AuthenticationToken> authenticationTokens = new HashMap<>();

    @Override
    public AuthenticationToken findToken(String token)
            throws RepositoryClientException, RepositoryServerException {
        return authenticationTokens.get(token);
    }

    @Override
    public boolean persistToken(AuthenticationToken authenticationToken)
            throws RepositoryClientException, RepositoryServerException {
        String token = authenticationToken.getToken();
        if (authenticationTokens.containsKey(token)) {
            return false;
        }

        authenticationTokens.put(token, authenticationToken);
        return true;
    }

    @Override
    public boolean revokeToken(String token) throws RepositoryClientException, RepositoryServerException {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void print() {
        System.out.println("Existing Tokens: " + authenticationTokens.keySet());
    }

}
