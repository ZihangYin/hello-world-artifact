package com.unicorn.rest.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.unicorn.rest.commons.ServiceConstants;


public class UserPassAuthenticationHelper {

    public static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    public static final int HASHING_ITERATIONS = 100;

    /*
     * This method is used to verify if the password persisted in database matches one provided by user
     */
    public static boolean authenticatePassword(@Nonnull String userPassword, @Nonnull ByteBuffer persistedPassword, @Nonnull ByteBuffer persistedSalt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return Arrays.equals(persistedPassword.array(), generateHashedPass(userPassword, persistedSalt));
    }

    /**
     * This method is used to generate a new hashed password for plain-text password provided by user and a randomly generated 
     * @param userPassword @Nonnull
     * @return ByteBuffer @Nonnull
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static @Nonnull ByteBuffer generateHashedPassword(@Nonnull String userPassword) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return generateHashedPassWithSalt(userPassword, generateRandomSalt());

    }
    
    /**
     * This method is used to generate a new hashed password for plain-text password and salt provided by user 
     * @param userPassword @Nonnull
     * @param salt @Nonnull
     * @return ByteBuffer @Nonnull
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static @Nonnull ByteBuffer generateHashedPassWithSalt(@Nonnull String userPassword, @Nonnull ByteBuffer salt) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return convertByteArrayToByteBuffer(generateHashedPass(userPassword, salt));
    }

    /**
     * This method is used to generate a random salt
     * @return ByteBuffer @Nonnull
     */
    public static @Nonnull ByteBuffer generateRandomSalt() {
        UUID salt = UUIDGenerator.randomUUID();
        ByteBuffer saltByteBuffer = ByteBuffer.wrap(new byte[16]);
        saltByteBuffer.putLong(salt.getMostSignificantBits());
        saltByteBuffer.putLong(salt.getLeastSignificantBits());

        // Prepare buffer to be read by resetting the positions to the beginning.
        saltByteBuffer.clear();
        return saltByteBuffer;
    }

    private static @Nonnull byte[] generateHashedPass(@Nonnull String userPassword, @Nonnull ByteBuffer salt) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return hashingPassword(HASHING_ITERATIONS, userPassword, salt.array());
    }

    private static @Nonnull byte[] hashingPassword(@Nonnull int numOfIterations, @Nonnull String password, @Nonnull byte[] salt) 
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

    private static @Nonnull ByteBuffer convertByteArrayToByteBuffer(@Nonnull byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        // Prepare buffer to be read by resetting the positions to the beginning.
        byteBuffer.clear();
        return byteBuffer;
    }
}
