package com.unicorn.rest.repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Singleton;

import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.UserAuthorizationInfo;

@Singleton
public interface UserAuthorizationRepository extends BasicAuthorizationRepository {
    
    public @Nullable UserAuthorizationInfo getUserAuthorizationInfo(@Nullable String userName) throws RepositoryClientException, RepositoryServerException;
    public @Nonnull String createNewUser(@Nullable UserAuthorizationInfo userAuthorizationInfo) throws RepositoryClientException, RepositoryServerException;
    
}
