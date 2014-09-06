package com.unicorn.rest.oauth.provider.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.validator.ValidatorException;
import org.glassfish.jersey.internal.util.Base64;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.unicorn.rest.activities.exception.InternalServerErrorException;
import com.unicorn.rest.activity.model.ErrorResponse;
import com.unicorn.rest.activity.model.OAuthRevokeTokenRequest;
import com.unicorn.rest.activity.model.OAuthTokenRequest;
import com.unicorn.rest.activity.model.OAuthTokenResponse;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrCode;
import com.unicorn.rest.activity.model.OAuthErrors.OAuthErrDescFormatter;
import com.unicorn.rest.activity.model.OAuthTokenRequest.GrantType;
import com.unicorn.rest.repository.exception.DuplicateKeyException;
import com.unicorn.rest.repository.exception.ItemNotFoundException;
import com.unicorn.rest.repository.exception.RepositoryClientException;
import com.unicorn.rest.repository.exception.RepositoryServerException;
import com.unicorn.rest.repository.impl.AuthenticationTokenRepositoryImpl;
import com.unicorn.rest.repository.impl.UserRepositoryImpl;
import com.unicorn.rest.repository.model.UserAuthorizationInfo;
import com.unicorn.rest.repository.model.AuthenticationToken.AuthenticationTokenType;
import com.unicorn.rest.server.GrizzlyServerTestBase;
import com.unicorn.rest.server.filter.model.AuthenticationMethod;
import com.unicorn.rest.server.injector.TestRepositoryBinder;
import com.unicorn.rest.utils.PasswordAuthenticationHelper;
import com.unicorn.rest.utils.SimpleFlakeKeyGenerator;

public class OAuthTokenActivitiesTest extends GrizzlyServerTestBase {
    
    private static WebTarget webTarget;
    private static TestRepositoryBinder repositoryBinder;
    
    @BeforeClass
    public static void setUpWebServer() throws Exception {
        repositoryBinder = new TestRepositoryBinder();
        setUpHttpsWebServer(repositoryBinder);
        webTarget = client.target(uri);
    }
    
    @After
    public void clearMockedRepository() {
        /*
         * Reset the mocking on this object so that the field can be safely re-used between tests.
         */
        Mockito.reset(repositoryBinder.getMockedTokenRepository());
        Mockito.reset(repositoryBinder.getMockedUserRepository());
    }

    private void mockUserAuthenticationHappyCase(String loginName, String password) 
            throws ValidatorException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryClientException, RepositoryServerException {
        UserRepositoryImpl mockedUserRepository = repositoryBinder.getMockedUserRepository();
        ByteBuffer salt = PasswordAuthenticationHelper.generateRandomSalt();
        ByteBuffer hashedPassword = PasswordAuthenticationHelper.generateHashedPassWithSalt(password, salt);
        UserAuthorizationInfo userAuthorizationInfo = UserAuthorizationInfo.buildUserAuthorizationInfo()
                .userId(1L).password(hashedPassword).salt(salt).build();
        Long userId = SimpleFlakeKeyGenerator.generateKey();
        Mockito.doReturn(userId).when(mockedUserRepository).getUserIdFromLoginName(loginName);
        Mockito.doReturn(userAuthorizationInfo).when(mockedUserRepository).getUserAuthorizationInfo(userId);
    }
    
    private void mockUserAuthenticationNoUser(String loginName, String password) 
            throws UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryClientException, RepositoryServerException {
        UserRepositoryImpl mockedUserRepository = repositoryBinder.getMockedUserRepository();
        ItemNotFoundException itemNotFound = new ItemNotFoundException();
        Mockito.doThrow(itemNotFound).when(mockedUserRepository).getUserIdFromLoginName(loginName);
    }
    
    private void mocTokenPersistencyDuplicateKeyOnce() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        DuplicateKeyException duplicateKey = new DuplicateKeyException();
        Mockito.doThrow(duplicateKey).doNothing().when(mockedTokenRepository).persistToken(Mockito.any());
    }
    
    private void mocTokenPersistencyHappyCase() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        Mockito.doNothing().when(mockedTokenRepository).persistToken(Mockito.any());
    }
    
    private void mocTokenPersistencyDuplicateKey() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        DuplicateKeyException duplicateKey = new DuplicateKeyException();
        Mockito.doThrow(duplicateKey).when(mockedTokenRepository).persistToken(Mockito.any());
    }
    
    private void mocTokenPersistencyClientError() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        RepositoryClientException badRequest = new RepositoryClientException("Bad Request");
        Mockito.doThrow(badRequest).when(mockedTokenRepository).persistToken(Mockito.any());
    }
    
    private void mocTokenPersistencyServerError() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        RepositoryServerException internalError = new RepositoryServerException("Internal Server Error", null);
        Mockito.doThrow(internalError).when(mockedTokenRepository).persistToken(Mockito.any());
    }
    
    private void mocTokenRevocationHappyCase() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        Mockito.doNothing().when(mockedTokenRepository).revokeToken(Mockito.any(), Mockito.any());
    }
    
    private void mocTokenRevocationClientError() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        RepositoryClientException badRequest = new RepositoryClientException("Bad Request");
        Mockito.doThrow(badRequest).when(mockedTokenRepository).revokeToken(Mockito.any(), Mockito.any());
    }
    
    private void mocTokenRevocationServerError() 
            throws RepositoryClientException, RepositoryServerException {
        AuthenticationTokenRepositoryImpl mockedTokenRepository = repositoryBinder.getMockedTokenRepository();
        RepositoryServerException internalError = new RepositoryServerException("Internal Server Error", null);
        Mockito.doThrow(internalError).when(mockedTokenRepository).revokeToken(Mockito.any(), Mockito.any());
    }
    
    /*
     * Happy Case
     */
    @Test
    public void testGenerateTokenForPasswordHappyCase() throws Exception {
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        mocTokenPersistencyHappyCase();
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthTokenResponse oauthTokenResponse = response.readEntity(OAuthTokenResponse.class);

        assertNotNull(oauthTokenResponse);
        assertEquals(AuthenticationTokenType.ACCESS_TOKEN.toString(), oauthTokenResponse.getTokenType());
        assertNotNull(oauthTokenResponse.getAccessToken());
        assertNotNull(oauthTokenResponse.getExpireAt());
        assertNull(oauthTokenResponse.getRefreshToken());
    }
    
    @Test
    public void testGenerateTokenForPasswordWithDuplicateTokenOnceHappyCase() throws Exception {
        
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        mocTokenPersistencyDuplicateKeyOnce();
        
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthTokenResponse oauthTokenResponse = response.readEntity(OAuthTokenResponse.class);
        assertNotNull(oauthTokenResponse);
        assertEquals(AuthenticationTokenType.ACCESS_TOKEN.toString(), oauthTokenResponse.getTokenType());
        assertNotNull(oauthTokenResponse.getAccessToken());
        assertNotNull(oauthTokenResponse.getExpireAt());
        assertNull(oauthTokenResponse.getRefreshToken());
    }

    @Test
    // TODO: Change this unit test once we have the code to support client credential
    public void testGenerateTokenForClientCredentialHappyCase() throws Exception {
        WebTarget clientCredentialWebTarget = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_CLIENT_ID, "client_id")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.CLIENT_CREDENTIAL.toString());
        Invocation.Builder invocationBuilder = clientCredentialWebTarget.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, 
                AuthenticationMethod.BASIC_AUTHENTICATION + Base64.encodeAsString("client_id:client_secret_proof"));

        Response response = invocationBuilder.get();
        
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        
        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.UNSUPPORTED_GRANT_TYPE.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), GrantType.CLIENT_CREDENTIAL.toString()), 
                errorResponse.getErrorDescription());
    }
    
    @Test
    // TODO: Change this unit test once we have the code to support authorization code
    public void testGenerateTokenForAuthorizationCodelHappyCase() throws Exception {
        WebTarget clientCredentialWebTarget = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_CLIENT_ID, "client_id")
                .queryParam(OAuthTokenRequest.OAUTH_REQUEST_TOKEN, "request_token").queryParam(OAuthTokenRequest.OAUTH_REDIRECT_URI, "redirect_url")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.AUTHORIZATION_CODE.toString());
        Invocation.Builder invocationBuilder = clientCredentialWebTarget.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, 
                AuthenticationMethod.BASIC_AUTHENTICATION + Base64.encodeAsString("client_id:client_secret_proof"));

        Response response = invocationBuilder.get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        
        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.UNSUPPORTED_GRANT_TYPE.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), GrantType.AUTHORIZATION_CODE.toString()), 
                errorResponse.getErrorDescription());
    }
    
    @Test
    public void testRevokeTokenHappyCase() throws Exception {
        mocTokenRevocationHappyCase();
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN_TYPE, AuthenticationTokenType.ACCESS_TOKEN.name())
                .queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN, "token").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNull(response.readEntity(Object.class));
    }
    
    /*
     * Bad Request
     */
    @Test
    public void testGenerateTokenMissingGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, "login_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "1a2b3c").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_GRANT_TYPE), 
                errorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenEmptyGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, "login_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "1a2b3c")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, new String()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_GRANT_TYPE), 
                errorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenUnsupportedGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, "login_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "1a2b3c")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, "unsupported_grant_type")
                .request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.UNSUPPORTED_GRANT_TYPE.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), "unsupported_grant_type"), 
                errorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForPasswordMissingloginName() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "1a2b3c")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), errorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_LOGIN_NAME), 
                errorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForPasswordMissingPassword() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, "login_name")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), errorResponse.getErrorCode());
        assertEquals(String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_PASSWORD), 
                errorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForPasswordUserDoesNotExist() throws Exception {
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationNoUser(loginName, password);
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.UNAUTHENTICATED_CLIENT.toString(), errorResponse.getErrorCode());
        assertEquals(String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), loginName), 
                errorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenForPasswordWrongPassword() throws Exception {
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        String wrongPassword = "wrong_password";
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, wrongPassword)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals("OAuthBadRequestException", errorResponse.getErrorType());
        assertEquals(OAuthErrCode.UNAUTHENTICATED_CLIENT.toString(), errorResponse.getErrorCode());
        assertEquals(String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), loginName), 
                errorResponse.getErrorDescription());
    }
    
    /*
     * Internal Server Errors
     */
    @Test
    public void testGenerateTokenFailedToPersisTokenDueToDuplicateToken() throws Exception {
        
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        mocTokenPersistencyDuplicateKey();
        
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        InternalServerErrorException expectedException = new InternalServerErrorException(null);
        assertEquals("InternalServerErrorException", errorResponse.getErrorType());
        assertEquals(expectedException.getErrorCode(), errorResponse.getErrorCode());
        assertEquals(expectedException.getErrorDescription(), errorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenFailedToPersisTokenDueToBadRequest() throws Exception {
        
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        mocTokenPersistencyClientError();
        
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        InternalServerErrorException expectedException = new InternalServerErrorException(null);
        assertEquals("InternalServerErrorException", errorResponse.getErrorType());
        assertEquals(expectedException.getErrorCode(), errorResponse.getErrorCode());
        assertEquals(expectedException.getErrorDescription(), errorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenFailedToPersisTokenDueToServerError() throws Exception {
        
        String loginName = "login_name";
        String password = "1a2b3c";
        mockUserAuthenticationHappyCase(loginName, password);
        mocTokenPersistencyServerError();
        
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_LOGIN_NAME, loginName)
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, password)
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        InternalServerErrorException expectedException = new InternalServerErrorException(null);
        assertEquals("InternalServerErrorException", errorResponse.getErrorType());
        assertEquals(expectedException.getErrorCode(), errorResponse.getErrorCode());
        assertEquals(expectedException.getErrorDescription(), errorResponse.getErrorDescription());
    }
    
    @Test
    public void testRevokeTokenFailedToRevokeTokenDueToBadRequest() throws Exception {
        
        mocTokenRevocationClientError();
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN_TYPE, AuthenticationTokenType.ACCESS_TOKEN.toString())
                .queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN, "token").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        InternalServerErrorException expectedException = new InternalServerErrorException(null);
        assertEquals("InternalServerErrorException", errorResponse.getErrorType());
        assertEquals(expectedException.getErrorCode(), errorResponse.getErrorCode());
        assertEquals(expectedException.getErrorDescription(), errorResponse.getErrorDescription());
    }
    
    @Test
    public void testRevokeTokenFailedToRevokeTokenDueToServerError() throws Exception {
        
        mocTokenRevocationServerError();
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN_TYPE, AuthenticationTokenType.ACCESS_TOKEN.toString())
                .queryParam(OAuthRevokeTokenRequest.OAUTH_TOKEN, "token").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        ErrorResponse errorResponse= response.readEntity(ErrorResponse.class);
        assertNotNull(errorResponse);
        InternalServerErrorException expectedException = new InternalServerErrorException(null);
        assertEquals("InternalServerErrorException", errorResponse.getErrorType());
        assertEquals(expectedException.getErrorCode(), errorResponse.getErrorCode());
        assertEquals(expectedException.getErrorDescription(), errorResponse.getErrorDescription());
    }
}
