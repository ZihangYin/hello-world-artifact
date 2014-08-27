package com.unicorn.rest.server.injector;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

import com.unicorn.rest.repository.AuthenticationTokenRepository;
import com.unicorn.rest.repository.UserAuthorizationRepository;
import com.unicorn.rest.repository.impl.AuthenticationTokenRepositoryImpl;
import com.unicorn.rest.repository.impl.UserAuthorizationRepositoryImpl;

public class TestRepositoryBinder extends AbstractBinder {

    private AuthenticationTokenRepositoryImpl mockedTokenRepository = Mockito.mock(AuthenticationTokenRepositoryImpl.class);
    private UserAuthorizationRepositoryImpl mockedUserRepository = Mockito.mock(UserAuthorizationRepositoryImpl.class);;

    protected void configure() {
        bind(mockedTokenRepository).to(AuthenticationTokenRepository.class);
        bind(mockedUserRepository).to(UserAuthorizationRepository.class);
    }

    public AuthenticationTokenRepositoryImpl getMockedTokenRepository() {
        return mockedTokenRepository;
    }

    public UserAuthorizationRepositoryImpl getMockedUserRepository() {
        return mockedUserRepository;
    }
}
