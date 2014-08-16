package com.pepsi.rest.oauth.provider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.junit.Test;

public class UserPassAuthenticationHelperTest {
    
    @Test
    public void generateRandomSaltHappyCase() throws UnsupportedEncodingException {
        ByteBuffer saltOne = UserPassAuthenticationHelper.generateRandomSalt();
        ByteBuffer saltTwo = UserPassAuthenticationHelper.generateRandomSalt();
        
        assertNotNull(saltOne);
        assertNotNull(saltTwo);
        
        UUID uuidOne = new UUID(saltOne.getLong(), saltOne.getLong());
        UUID uuidTwo = new UUID(saltTwo.getLong(), saltTwo.getLong());
        
        assertThat(saltOne.array(), not(equalTo(saltTwo.array())));
        assertNotEquals(uuidOne, uuidTwo);
    }
    
    @Test
    public void generateHashedPassWithSaltHappyCase() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordOne = "password";  
        ByteBuffer saltOne = UserPassAuthenticationHelper.generateRandomSalt();
        
        byte[] hashedPasswordOne = UserPassAuthenticationHelper.generateHashedPassWithSalt(passwordOne, saltOne);
        byte[] hashedPasswordDup = UserPassAuthenticationHelper.generateHashedPassWithSalt(passwordOne, saltOne);
        
        assertNotNull(hashedPasswordOne);
        assertThat(hashedPasswordOne, equalTo(hashedPasswordDup));
        
        String anotherPassword = "anotherPassword"; 
        byte[] hashedPasswordTwo = UserPassAuthenticationHelper.generateHashedPassWithSalt(anotherPassword, saltOne);
        
        assertNotNull(hashedPasswordTwo);
        assertThat(hashedPasswordOne, not(equalTo(hashedPasswordTwo)));
        
        ByteBuffer anotherSalt = UserPassAuthenticationHelper.generateRandomSalt();
        byte[] hashedPasswordThree = UserPassAuthenticationHelper.generateHashedPassWithSalt(anotherPassword, anotherSalt);
        
        assertNotNull(hashedPasswordThree);
        assertThat(hashedPasswordOne, not(equalTo(hashedPasswordThree)));
        assertThat(hashedPasswordTwo, not(equalTo(hashedPasswordThree)));
    }
    
    @Test
    public void generateHashedPasswordHappyCase() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordOne = "password";  
        ByteBuffer hashedPasswordOne = UserPassAuthenticationHelper.generateHashedPassword(passwordOne);
        ByteBuffer hashedPasswordDup = UserPassAuthenticationHelper.generateHashedPassword(passwordOne);
        
        assertNotNull(hashedPasswordOne);
        // Even if the password are the same, the randomly generate salt on the server side are different.
        // Therefore, the hashed password should be different
        assertThat(hashedPasswordOne, not(equalTo(hashedPasswordDup)));
        
        String anotherPassword = "anotherPassword"; 
        ByteBuffer hashedPasswordTwo = UserPassAuthenticationHelper.generateHashedPassword(anotherPassword);
        
        assertNotNull(hashedPasswordTwo);
        assertThat(hashedPasswordOne, not(equalTo(hashedPasswordTwo)));
    }
    
    @Test
    public void generateHashedPasswordForEmptyPassword() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordOne = "";  
        ByteBuffer hashedPasswordOne = UserPassAuthenticationHelper.generateHashedPassword(passwordOne);
        assertNotNull(hashedPasswordOne);
        
        String passwordTwo = new String(); 
        ByteBuffer hashedPasswordTwo = UserPassAuthenticationHelper.generateHashedPassword(passwordTwo);
        
        assertNotNull(hashedPasswordTwo);
        assertThat(hashedPasswordOne, not(equalTo(hashedPasswordTwo)));
    }
    
    @Test
    public void authenticatePasswordHappyCase() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordOne = "password";  
        ByteBuffer saltOne = UserPassAuthenticationHelper.generateRandomSalt();
        
        byte[] hashedPasswordOne = UserPassAuthenticationHelper.generateHashedPassWithSalt(passwordOne, saltOne);
        ByteBuffer hashedPassByteBufferOne = ByteBuffer.wrap(hashedPasswordOne);
        hashedPassByteBufferOne.clear();
        
        assertTrue(UserPassAuthenticationHelper.authenticatePassword(passwordOne, hashedPassByteBufferOne, saltOne));
        
        String passwordTwo = "anotherPassword";
        assertFalse(UserPassAuthenticationHelper.authenticatePassword(passwordTwo, hashedPassByteBufferOne, saltOne));
        
        ByteBuffer saltTwo = UserPassAuthenticationHelper.generateRandomSalt();
        assertFalse(UserPassAuthenticationHelper.authenticatePassword(passwordOne, hashedPassByteBufferOne, saltTwo));
        
    }
    
    @Test
    public void authenticatePasswordForEmptyPassword() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String passwordOne = "";  
        ByteBuffer saltOne = UserPassAuthenticationHelper.generateRandomSalt();
        
        byte[] hashedPasswordOne = UserPassAuthenticationHelper.generateHashedPassWithSalt(passwordOne, saltOne);
        ByteBuffer hashedPassByteBufferOne = ByteBuffer.wrap(hashedPasswordOne);
        hashedPassByteBufferOne.clear();
        
        assertTrue(UserPassAuthenticationHelper.authenticatePassword(passwordOne, hashedPassByteBufferOne, saltOne));
        
        String passwordTwo = new String();
        assertTrue(UserPassAuthenticationHelper.authenticatePassword(passwordTwo, hashedPassByteBufferOne, saltOne));
    }
}
