package com.pepsi.rest.repository.impl;

import java.util.HashMap;
import java.util.Map;

import com.pepsi.rest.repository.UserAuthorizationRepository;
import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryServerException;
import com.pepsi.rest.repository.model.UserAuthorizationInfo;

public class UserAuthorizationRepositoryImpl implements UserAuthorizationRepository {
    
    private final Map<String, UserAuthorizationInfo> userAuthorizationInfos = new HashMap<>();
    
    @Override
    public UserAuthorizationInfo getUserAuthorizationInfo(String userName)
            throws RepositoryClientException, RepositoryServerException {
        return userAuthorizationInfos.get(userName);
    }

    @Override
    public String createNewUser(UserAuthorizationInfo userAuthorizationInfo)
            throws RepositoryClientException, RepositoryServerException {
        return null;
    }
}
