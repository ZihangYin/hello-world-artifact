package com.pepsi.rest.repository;

import java.util.List;

import javax.annotation.Nullable;

public interface BasicAuthorizationRepository extends AuthorizationRepository {
    
    public abstract void authenticate(@Nullable String principal, @Nullable String credential);
    public abstract void authorize(@Nullable String principal, @Nullable String credential, @Nullable List<String> scope);
}
