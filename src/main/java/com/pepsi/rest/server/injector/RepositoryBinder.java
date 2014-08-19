package com.pepsi.rest.server.injector;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.pepsi.rest.repository.AuthenticationTokenRepository;
import com.pepsi.rest.repository.UserAuthorizationRepository;
import com.pepsi.rest.repository.impl.AuthenticationTokenRepositoryImpl;
import com.pepsi.rest.repository.impl.UserAuthorizationRepositoryImpl;

public class RepositoryBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(new AuthenticationTokenRepositoryImpl()).to(AuthenticationTokenRepository.class);
        bind(new UserAuthorizationRepositoryImpl()).to(UserAuthorizationRepository.class);
    }
}
