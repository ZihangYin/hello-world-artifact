package com.pepsi.rest.server.filter.utils;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;

import com.pepsi.rest.activities.exception.AccessDeniedException;
import com.pepsi.rest.repository.AuthorizationRepository;
import com.pepsi.rest.repository.BearerAuthorizationRepository;
import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryMismatchException;
import com.pepsi.rest.repository.exception.RepositoryServerException;

public class BearerAuthorizer extends Authorizer {
    private static final Logger LOG = LogManager.getLogger(BearerAuthorizer.class);
    private static final String AUTHORIZATION_CODE_SEPARATOR = ":";
    
    private String token;
    private String clientSecretProof;

    public BearerAuthorizer(@Nonnull String authorizationCode) throws AccessDeniedException {
        super(authorizationCode);
    }

    @Override
    protected @Nonnull String[] parseAuthorizationCode (@Nonnull String authorizationCode) throws AccessDeniedException {
        //Decode the Base64 into byte[]
        String[] authentication = Base64.decodeAsString(authorizationCode).split(AUTHORIZATION_CODE_SEPARATOR);
        if (authentication.length > 2 || StringUtils.isBlank(authentication[0])) {
            throw new AccessDeniedException();
        } 
        this.token = authentication[0];
        if (authentication.length == 2 && !StringUtils.isBlank(authentication[1])) {
            this.clientSecretProof = authentication[1];
        }
        return authentication;
    }

    @Override
    public void authenticate(@Nullable AuthorizationRepository authorizationRepository) throws RepositoryClientException, RepositoryServerException {
        BearerAuthorizationRepository bearerAuthorizationRepository = retrieveBearerAuthorizationRepository(authorizationRepository);
        bearerAuthorizationRepository.authenticate(token, clientSecretProof);
    }

    @Override
    public void authorize(@Nullable AuthorizationRepository authorizationRepository, @Nullable List<String> scope) throws RepositoryClientException, RepositoryServerException {
        BearerAuthorizationRepository bearerAuthorizationRepository = retrieveBearerAuthorizationRepository(authorizationRepository);
        bearerAuthorizationRepository.authorize(token, clientSecretProof, scope);
    }

    private @Nonnull BearerAuthorizationRepository retrieveBearerAuthorizationRepository(@Nullable AuthorizationRepository authorizationRepository) 
            throws RepositoryMismatchException {

        if (authorizationRepository instanceof BearerAuthorizationRepository) {
            return (BearerAuthorizationRepository) authorizationRepository;
        } else {
            LOG.error("Failed to retrieve bearer authorization repository from given repository type: " + authorizationRepository.getClass());
            throw new RepositoryMismatchException();
        }
    }

    public @Nonnull String getToken() {
        return token;
    }

    public @Nullable String getClientSecretProof() {
        return clientSecretProof;
    }
}
