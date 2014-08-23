package com.pepsi.rest.repository.model;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

public class UserAuthorizationInfo {
    
    private final String userName;
    private final ByteBuffer hashedPassword;
    private final ByteBuffer salt;
    
    public static UserAuthorizationInfoBuilder buildUserAuthorizationInfo() {
        return new UserAuthorizationInfoBuilder();
    }
    
    private UserAuthorizationInfo(String userName, ByteBuffer hashedPassword,
            ByteBuffer salt) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public @Nonnull String getUserName() {
        return userName;
    }

    public @Nonnull ByteBuffer getHashedPassword() {
        return hashedPassword;
    }

    public @Nonnull ByteBuffer getSalt() {
        return salt;
    }
    
    public static class UserAuthorizationInfoBuilder {
        private String userName;
        private ByteBuffer hashedPassword;
        private ByteBuffer salt;
        
        public UserAuthorizationInfoBuilder() {}

        public UserAuthorizationInfoBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }
        
        public UserAuthorizationInfoBuilder hashedPassword(ByteBuffer hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }
        
        public UserAuthorizationInfoBuilder salt(ByteBuffer salt) {
            this.salt = salt;
            return this;
        }
        
        public UserAuthorizationInfo build() {
            if (StringUtils.isBlank(userName) || hashedPassword == null || salt == null) {
                throw new IllegalArgumentException("Failed while attempting to build user authorization info due to missing required parameters");
            }
            
            return new UserAuthorizationInfo(userName, hashedPassword, salt);
        }
    }
}
