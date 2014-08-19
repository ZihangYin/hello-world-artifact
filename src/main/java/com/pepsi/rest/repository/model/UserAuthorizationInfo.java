package com.pepsi.rest.repository.model;

import java.nio.ByteBuffer;

public class UserAuthorizationInfo {
    
    private final String userName;
    private final ByteBuffer hashedPassword;
    private final ByteBuffer salt;
    
    public UserAuthorizationInfo(String userName, ByteBuffer hashedPassword,
            ByteBuffer salt) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public String getUserName() {
        return userName;
    }

    public ByteBuffer getHashedPassword() {
        return hashedPassword;
    }

    public ByteBuffer getSalt() {
        return salt;
    }
}
