package com.unicorn.rest.repository.impl;

import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unicorn.rest.repository.UserAuthorizationRepository;
import com.unicorn.rest.repository.exception.ItemNotFoundException;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.model.UserAuthorizationInfo;
import com.unicorn.rest.utils.SimpleFlakeKeyGenerator;

public class UserAuthorizationRepositoryImpl implements UserAuthorizationRepository {

    private static final String USER_AUTHORIZATION_TABLE_NAME = "USER_AUTHORIZATION";
    private static final String USER_ID_PRIMARY_KEY = "USER_ID";
    private static final String USER_NAME_INDEX_KEY = "USER_NAME";
    private static final String EMAIL_ADDRESS_INDEX_KEY = "EMAIL_ADDRESS";
    private static final String MOBILE_PHONE_INDEX_KEY = "MOBILE_PHONE";
    private static final String PASSWORD_KEY = "PASSWORD";
    private static final String SALT_KEY = "SALT";

    @Override
    public void authenticate(@Nullable String principal, @Nullable String credential) {
    }

    @Override
    public void authorize(@Nullable String principal, @Nullable String credential,
            @Nullable List<String> scope) {
    }

    @Override
    public @Nullable UserAuthorizationInfo getUserAuthorizationInfo(@Nullable String userName)
            throws ItemNotFoundException, RepositoryClientException, RepositoryServerException {
        return null;
    }

    @Override
    public @Nonnull String createNewUser(@Nullable UserAuthorizationInfo userAuthorizationInfo)
            throws RepositoryClientException, RepositoryServerException {

        /**
         * Simple-flake versus Snow-flake
         * https://blog.twitter.com/2010/announcing-snowflake
         * http://engineering.custommade.com/simpleflake-distributed-id-generation-for-the-lazy/
         * http://instagram-engineering.tumblr.com/post/10853187575/sharding-ids-at-instagram
         *  
         * For the sake of simplicity, we prefer the simple-flake approach for now.
         * More detail, refer to SimpleFlakeKeyGenerator class.
         */ 
        long userId = SimpleFlakeKeyGenerator.generateKey();
        String userName = userAuthorizationInfo.getUserName();
        ByteBuffer password = userAuthorizationInfo.getHashedPassword();
        ByteBuffer salt = userAuthorizationInfo.getSalt();
        
        return null;
    }

    public static void main (String[] args) {
    }
}
