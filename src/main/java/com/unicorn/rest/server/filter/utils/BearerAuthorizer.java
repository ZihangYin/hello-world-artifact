package com.unicorn.rest.server.filter.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;

import com.unicorn.rest.activities.exception.AccessDeniedException;
import com.unicorn.rest.repository.AuthenticationTokenRepository;
import com.unicorn.rest.repository.exception.InvalidRequestException;
import com.unicorn.rest.repository.exception.ItemNotFoundException;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.AuthenticationToken;
import com.unicorn.rest.repository.model.AuthenticationToken.AuthenticationTokenType;
import com.unicorn.rest.server.filter.model.UserSubject;

public class BearerAuthorizer {
    private static final String AUTHORIZATION_CODE_SEPARATOR = ":";
    
    public static UserSubject authenticate(@Nonnull String authorizationCode, @Nonnull AuthenticationTokenRepository authenticationTokenRepository) 
            throws AccessDeniedException, RepositoryServerException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryClientException {
        //Decode the Base64 into byte[]
        String[] authentication = Base64.decodeAsString(authorizationCode).split(AUTHORIZATION_CODE_SEPARATOR);
        if (authentication.length > 2 || StringUtils.isBlank(authentication[0])) {
            throw new AccessDeniedException();
        } 
        String token = authentication[0];
        // TODO: verify if the client secret proof matches with record in the 
        if (authentication.length == 2 && !StringUtils.isBlank(authentication[1])) {}
        return authenticateToken(token, authenticationTokenRepository);
    }

    private static UserSubject authenticateToken(@Nonnull String token, @Nonnull AuthenticationTokenRepository authenticationTokenRepository) 
            throws AccessDeniedException, RepositoryServerException, RepositoryClientException{
        try {
            AuthenticationToken authenticationToken = authenticationTokenRepository.findToken(AuthenticationTokenType.ACCESS_TOKEN, token);
            return new UserSubject(authenticationToken.getUserId());
        } catch (InvalidRequestException | ItemNotFoundException error) {
            throw new AccessDeniedException();
        }
    }
}
