package com.pepsi.rest.oauth.provider;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pepsi.rest.oauth.provider.model.OAuthErrorResponse;
import com.pepsi.rest.oauth.provider.model.OAuthRequestQueryParameters;
import com.pepsi.rest.oauth.provider.model.OAuthRequestValidator;
import com.pepsi.rest.oauth.provider.model.OAuthTokenActivityError;
import com.pepsi.rest.oauth.provider.model.OAuthTokenResponse;
import com.pepsi.rest.server.GrizzlyServerTestBase;

public class OAuthTokenActivityTest extends GrizzlyServerTestBase {

    private static WebTarget webTarget;

    @BeforeClass
    public static void setUpWebServer() throws Exception {       
        setUpHttpsWebServer();

        Client client = getHttpsClient();
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        webTarget = client.target(uri);
    }

    @Test
    public void testGenerateTokenHappyCase() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_USER_NAME, "username")
                .queryParam(OAuthRequestQueryParameters.OAUTH_PASSWORD, "password")
                .queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, "password").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthTokenResponse oauthTokenResponse = response.readEntity(OAuthTokenResponse.class);
        assertNotNull(oauthTokenResponse);
        assertNotNull(oauthTokenResponse.getAccessToken());
        assertTrue(oauthTokenResponse.getExpireInSeconds() > 0);
    }

    @Test
    public void testGenerateTokenMissingGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_USER_NAME, "username")
                .queryParam(OAuthRequestQueryParameters.OAUTH_PASSWORD, "password").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthRequestValidator.MISSING_PARAMETERS, oauthErrorResponse.getErrorCode());
        assertEquals( String.format("Missing values for required parameters: %s", OAuthRequestQueryParameters.OAUTH_GRANT_TYPE), 
                oauthErrorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenEmptyGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_USER_NAME, "username")
                .queryParam(OAuthRequestQueryParameters.OAUTH_PASSWORD, "password")
                .queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, new String()).request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthRequestValidator.MISSING_PARAMETERS, oauthErrorResponse.getErrorCode());
        assertEquals( String.format("Missing values for required parameters: %s", OAuthRequestQueryParameters.OAUTH_GRANT_TYPE), 
                oauthErrorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenUnsupportedGrantType() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_USER_NAME, "username")
                .queryParam(OAuthRequestQueryParameters.OAUTH_PASSWORD, "password")
                .queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, "unsupported_grant_type")
                .request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthTokenActivityError.UNSUPPORTED_GRANT_TYPE, oauthErrorResponse.getErrorCode());
        assertEquals("The authorization grant type is not supported by the authorization server", 
                oauthErrorResponse.getErrorDescription());
    }

    @Test
    public void testGenerateTokenMissingUserName() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_PASSWORD, "password")
                .queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, "password").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthRequestValidator.MISSING_PARAMETERS, oauthErrorResponse.getErrorCode());
        assertEquals( String.format("Missing values for required parameters: %s", OAuthRequestQueryParameters.OAUTH_USER_NAME), 
                oauthErrorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenMissingPassword() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_USER_NAME, "username")
                .queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, "password").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthRequestValidator.MISSING_PARAMETERS, oauthErrorResponse.getErrorCode());
        assertEquals(String.format("Missing values for required parameters: %s", OAuthRequestQueryParameters.OAUTH_PASSWORD), 
                oauthErrorResponse.getErrorDescription());
    }
    
    @Test
    public void testGenerateTokenMissingUserNamePassword() throws Exception {
        Response response = webTarget.path("oauth2/v1/token").queryParam(OAuthRequestQueryParameters.OAUTH_GRANT_TYPE, "password").request(MediaType.APPLICATION_JSON).post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        OAuthErrorResponse oauthErrorResponse= response.readEntity(OAuthErrorResponse.class);
        assertNotNull(oauthErrorResponse);
        assertEquals(OAuthRequestValidator.MISSING_PARAMETERS, oauthErrorResponse.getErrorCode());
        assertEquals(String.format("Missing values for required parameters: %s, %s", OAuthRequestQueryParameters.OAUTH_USER_NAME, 
                OAuthRequestQueryParameters.OAUTH_PASSWORD), oauthErrorResponse.getErrorDescription());
    }
    
}
