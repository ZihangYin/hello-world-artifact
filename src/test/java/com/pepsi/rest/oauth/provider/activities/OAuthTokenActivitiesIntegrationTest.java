package com.pepsi.rest.oauth.provider.activities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pepsi.rest.activities.exception.MissingAuthorizationException;
import com.pepsi.rest.activity.model.OAuthErrorResponse;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrDescFormatter;
import com.pepsi.rest.activity.model.OAuthTokenRequest;
import com.pepsi.rest.activity.model.OAuthTokenRequest.GrantType;
import com.pepsi.rest.activity.model.OAuthTokenResponse;
import com.pepsi.rest.repository.model.AuthenticationToken.AuthenticationTokenType;
import com.pepsi.rest.server.GrizzlyServerIntegrationTestBase;
import com.pepsi.rest.server.filter.utils.Authorizer.AuthenticationMethod;

public class OAuthTokenActivitiesIntegrationTest extends GrizzlyServerIntegrationTestBase {

    private static WebTarget webTarget;
    
    @BeforeClass
    public static void setUpWebServer() throws Exception {
        setUpHttpsWebServer();
        webTarget = client.target(uri);
    }

    @Test
    public void testGenerateTokenMissingGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_USER_NAME, "user_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "password").request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), oauthErrorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_GRANT_TYPE), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenEmptyGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_USER_NAME, "user_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "password")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, new String()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), oauthErrorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_GRANT_TYPE), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenUnsupportedGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_USER_NAME, "user_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "password")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, "unsupported_grant_type")
                .request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.UNSUPPORTED_GRANT_TYPE.toString(), oauthErrorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), "unsupported_grant_type"), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForPasswordHappyCase() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_USER_NAME, "user_name")
                .queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "password")
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
    public void testGenerateTokenForPasswordMissingUserName() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_PASSWORD, "password")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), oauthErrorResponse.getErrorCode());
        assertEquals( String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_USER_NAME), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForPasswordMissingPassword() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_USER_NAME, "user_name")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.PASSWORD.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.MISSING_PARAMETERS.toString(), oauthErrorResponse.getErrorCode());
        assertEquals(String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), OAuthTokenRequest.OAUTH_PASSWORD), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    // TODO: Change this unit test once we have the code to authorize client based on authorization header
    public void testGenerateTokenForClientCredentialHappyCase() throws Exception {
        WebTarget clientCredentialWebTarget = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_CLIENT_ID, "client_id")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.CLIENT_CREDENTIAL.toString());
        Invocation.Builder invocationBuilder = clientCredentialWebTarget.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, 
                AuthenticationMethod.BASIC_AUTHENTICATION + Base64.encodeAsString("client_id:client_secret_proof"));

        Response response = invocationBuilder.get();
        
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
        
        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthErrCode.UNAUTHENTICATED_CLIENT.toString(), oauthErrorResponse.getErrorCode());
        assertEquals(String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), "client_id"), 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenForClientCredentialMissingAuthorizationHeader() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthTokenRequest.OAUTH_CLIENT_ID, "client_id")
                .queryParam(OAuthTokenRequest.OAUTH_GRANT_TYPE, GrantType.CLIENT_CREDENTIAL.toString()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        String errMsg= response.readEntity(String.class);
        assertEquals(new MissingAuthorizationException().getMessage(), errMsg);
    }

}
