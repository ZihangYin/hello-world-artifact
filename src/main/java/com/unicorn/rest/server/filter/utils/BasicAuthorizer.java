package com.unicorn.rest.server.filter.utils;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import com.unicorn.rest.activities.exception.AccessDeniedException;
import com.unicorn.rest.repository.AuthorizationRepository;
import com.unicorn.rest.repository.BasicAuthorizationRepository;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryMismatchException;
import com.unicorn.rest.repository.exception.RepositoryServerException;

public class BasicAuthorizer extends Authorizer {
    private static final Logger LOG = LogManager.getLogger(BasicAuthorizer.class);
    private static final String AUTHORIZATION_CODE_SEPARATOR = ":";
    
    @Getter @Nonnull private String principal;
    @Getter @Nonnull private String credential;
    
    public BasicAuthorizer(@Nonnull String authorizationCode) throws AccessDeniedException {
        super(authorizationCode);
    }

    @Override
    protected @Nonnull String[] parseAuthorizationCode (@Nonnull String authorizationCode) throws AccessDeniedException {
        //Decode the Base64 into byte[]
        String[] authentication = Base64.decodeAsString(authorizationCode).split(AUTHORIZATION_CODE_SEPARATOR);
        if (authentication.length != 2 || StringUtils.isBlank(authentication[0]) || StringUtils.isBlank(authentication[1])) {
            
            throw new AccessDeniedException();
        }
        this.principal = authentication[0];
        this.credential = authentication[1];
        
        return authentication;
    }
    
    @Override
    public void authenticate(@Nullable AuthorizationRepository authorizationRepository) throws RepositoryClientException, RepositoryServerException {
        BasicAuthorizationRepository basicAuthorizationRepository = retrieveBasicAuthorizationRepository(authorizationRepository);
        basicAuthorizationRepository.authenticate(principal, credential);
        
        
    }
    
    @Override
    public void authorize(@Nullable AuthorizationRepository authorizationRepository, @Nullable List<String> scope) throws RepositoryClientException, RepositoryServerException {
        BasicAuthorizationRepository basicAuthorizationRepository = retrieveBasicAuthorizationRepository(authorizationRepository);
        basicAuthorizationRepository.authorize(principal, credential, scope);
    }
    
    private @Nonnull BasicAuthorizationRepository retrieveBasicAuthorizationRepository(@Nullable AuthorizationRepository authorizationRepository) 
            throws RepositoryMismatchException {

        if (authorizationRepository instanceof BasicAuthorizationRepository) {
            return (BasicAuthorizationRepository) authorizationRepository;
        } else {
            LOG.error("Failed to retrieve basic authorization repository from given repository type: " + authorizationRepository.getClass());
            throw new RepositoryMismatchException();
        }
    }
}
