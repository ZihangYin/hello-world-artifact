package com.pepsi.rest.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pepsi.rest.repository.UserAuthorizationRepository;
import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryServerException;
import com.pepsi.rest.repository.model.UserAuthorizationInfo;

public class UserAuthorizationRepositoryImpl implements UserAuthorizationRepository {
    
    private final Map<String, UserAuthorizationInfo> userAuthorizationInfos = new HashMap<>();

    @Override
    public void authenticate(@Nullable String principal, @Nullable String credential) {
        // TODO Auto-generated method stub
    }

    @Override
    public void authorize(@Nullable String principal, @Nullable String credential,
            @Nullable List<String> scope) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public @Nullable UserAuthorizationInfo getUserAuthorizationInfo(@Nullable String userName)
            throws RepositoryClientException, RepositoryServerException {
        return userAuthorizationInfos.get(userName);
    }

    @Override
    public @Nonnull String createNewUser(@Nullable UserAuthorizationInfo userAuthorizationInfo)
            throws RepositoryClientException, RepositoryServerException {
        return null;
    }
}
