package com.unicorn.rest.server.injector;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.unicorn.rest.repository.AuthenticationTokenRepository;
import com.unicorn.rest.repository.UserAuthorizationRepository;
import com.unicorn.rest.repository.impl.AuthenticationTokenRepositoryImpl;
import com.unicorn.rest.repository.impl.UserAuthorizationRepositoryImpl;

public class RepositoryBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(new AuthenticationTokenRepositoryImpl()).to(AuthenticationTokenRepository.class);
        bind(new UserAuthorizationRepositoryImpl()).to(UserAuthorizationRepository.class);
        
    }
}
