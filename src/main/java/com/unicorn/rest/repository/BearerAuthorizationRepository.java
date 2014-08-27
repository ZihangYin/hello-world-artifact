package com.unicorn.rest.repository;

import java.util.List;

import javax.annotation.Nullable;

public interface BearerAuthorizationRepository extends AuthorizationRepository {
    
    public abstract void authenticate(@Nullable String token, @Nullable String clientSecretProof);
    public abstract void authorize(@Nullable String token, @Nullable String clientSecretProof, @Nullable List<String> scope);
}
