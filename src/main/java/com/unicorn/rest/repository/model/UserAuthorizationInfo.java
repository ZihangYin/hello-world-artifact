package com.unicorn.rest.repository.model;

import java.nio.ByteBuffer;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAuthorizationInfo {
    
    @Getter @Nonnull private final String userName;
    @Getter @Nonnull private final ByteBuffer hashedPassword;
    @Getter @Nonnull private final ByteBuffer salt;
    @Getter private final String emailAddress;
    @Getter private final String mobilePhone;
    
    public static UserAuthorizationInfoBuilder buildUserAuthorizationInfo() {
        return new UserAuthorizationInfoBuilder();
    }
    
    public static class UserAuthorizationInfoBuilder {
        private String userName;
        private ByteBuffer hashedPassword;
        private ByteBuffer salt;
        private String emailAddress;
        private String mobilePhone;
        
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
        
        public UserAuthorizationInfoBuilder emailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }
        
        public UserAuthorizationInfoBuilder mobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
            return this;
        }
        
        public UserAuthorizationInfo build() {
            if (StringUtils.isBlank(userName) || hashedPassword == null || salt == null) {
                throw new IllegalArgumentException("Failed while attempting to build user authorization info due to missing required parameters");
            }
            
            return new UserAuthorizationInfo(userName, hashedPassword, salt, emailAddress, mobilePhone);
        }
    }
}
