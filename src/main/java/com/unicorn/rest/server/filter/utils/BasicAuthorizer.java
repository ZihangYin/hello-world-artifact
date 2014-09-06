package com.unicorn.rest.server.filter.utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;

import com.unicorn.rest.activities.exception.AccessDeniedException;
import com.unicorn.rest.repository.UserRepository;
import com.unicorn.rest.repository.exception.InvalidRequestException;
import com.unicorn.rest.repository.exception.ItemNotFoundException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.UserAuthorizationInfo;
import com.unicorn.rest.server.filter.model.UserSubject;
import com.unicorn.rest.utils.PasswordAuthenticationHelper;

public class BasicAuthorizer {
    private static final String AUTHORIZATION_CODE_SEPARATOR = ":";

    public static UserSubject authenticate(@Nonnull String authorizationCode, @Nonnull UserRepository userRepository) 
            throws AccessDeniedException, RepositoryServerException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //Decode the Base64 into byte[]
        String[] authentication = Base64.decodeAsString(authorizationCode).split(AUTHORIZATION_CODE_SEPARATOR);
        if (authentication.length != 2 || StringUtils.isBlank(authentication[0]) || StringUtils.isBlank(authentication[1])) {
            throw new AccessDeniedException();
        }
        return authenticate(authentication[0], authentication[1], userRepository);
    }

    public static UserSubject authenticate(@Nonnull String principal, @Nonnull String credential, @Nonnull UserRepository userRepository) 
            throws AccessDeniedException, RepositoryServerException, UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            Long userId = userRepository.getUserIdFromLoginName(principal);
            UserAuthorizationInfo userAuthorizationInfo = userRepository.getUserAuthorizationInfo(userId);

            if (PasswordAuthenticationHelper.authenticatePassword(credential, userAuthorizationInfo.getPassword(), userAuthorizationInfo.getSalt())) {
                return new UserSubject(userId);
            }

            throw new AccessDeniedException();
        } catch (InvalidRequestException | ItemNotFoundException error) {
            throw new AccessDeniedException();
        }
    }
}
