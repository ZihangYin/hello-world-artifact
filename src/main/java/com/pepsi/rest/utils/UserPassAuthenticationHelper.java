package com.pepsi.rest.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.pepsi.rest.commons.UUIDGenerator;
import com.pepsi.rest.commons.ServiceConstants;


public class UserPassAuthenticationHelper {
    
    public static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    public static final int HASHING_ITERATIONS = 100;
    
    /*
     * This method is used to verify if the password persisted in database matches one provided by user
     */
    public static boolean authenticatePassword(@Nonnull String userPassword, @Nonnull ByteBuffer persistedPassword, @Nonnull ByteBuffer persistedSalt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return Arrays.equals(persistedPassword.array(), generateHashedPassWithSalt(userPassword, persistedSalt));
    }
    
    /*
     * This method is used to generate a new hashed password for plain-text password provided by user and a randomly generated 
     */
    public static @Nonnull ByteBuffer generateHashedPassword(@Nonnull String userPassword) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ByteBuffer salt = generateRandomSalt();
        byte[] hashedPassword = generateHashedPassWithSalt(userPassword, salt);
        ByteBuffer hashedPassByteBuffer = ByteBuffer.wrap(hashedPassword);
        
        // Prepare buffer to be read by resetting the positions to the beginning.
        hashedPassByteBuffer.clear();
        return hashedPassByteBuffer;
        
    }
    
    protected static @Nonnull byte[] generateHashedPassWithSalt(@Nonnull String userPassword, @Nonnull ByteBuffer salt) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return hashingPassword(HASHING_ITERATIONS, userPassword, salt.array());
    }

    protected static ByteBuffer generateRandomSalt() {
        
        UUID salt = UUIDGenerator.randomUUID();
        ByteBuffer saltByteBuffer = ByteBuffer.wrap(new byte[16]);
        saltByteBuffer.putLong(salt.getMostSignificantBits());
        saltByteBuffer.putLong(salt.getLeastSignificantBits());
        
        // Prepare buffer to be read by resetting the positions to the beginning.
        saltByteBuffer.clear();
        return saltByteBuffer;
    }
    
    private static byte[] hashingPassword(int numOfIterations, String password, byte[] salt) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        
        // TODO: Monitoring time elapse for hashing process
        MessageDigest msgDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        msgDigest.update(salt);
        byte[] hashedPass = msgDigest.digest(password.getBytes(ServiceConstants.UTF_8_CHARSET));
        for (int i = 0; i < numOfIterations; i++) {
            msgDigest.reset();
            hashedPass = msgDigest.digest(hashedPass);
        }
        return hashedPass;
    }
    
}
