package com.pepsi.rest.repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryServerException;
import com.pepsi.rest.repository.model.UserAuthorizationInfo;

@Singleton
public interface UserAuthorizationRepository {
    
    public @Nullable UserAuthorizationInfo getUserAuthorizationInfo(@Nullable String userName) throws RepositoryClientException, RepositoryServerException;
    public @Nonnull String createNewUser(@Nullable UserAuthorizationInfo userAuthorizationInfo) throws RepositoryClientException, RepositoryServerException;
    
}
