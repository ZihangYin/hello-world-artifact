package com.pepsi.rest.server.filter;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

public class ActivitySecurityFilterTest extends GrizzlyServerTestBase {
    
    private Client client;
    
    @BeforeClass
    public static void setUpServer() throws Exception {        
        setUpHttpsWebServer();
    }
    
    @Before
    public void setUpClient() throws Exception {        
        client = getHttpsClient(); 
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpHappyCase () {
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        WebTarget webTarget = client.target(uri);
        
        Response response =webTarget.path("api/v1/hello").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));      
    }
   
    @Test
    public void testActivitySecurityFilterOverHttpMissingAuthentication () {
        WebTarget webTarget = client.target(uri);
        
        Response response =webTarget.path("api/v1/hello").request().get();
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing.", response.readEntity(String.class));
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyUsername () {
        client.register(HttpAuthenticationFeature.basic("", "password"));        
        WebTarget webTarget = client.target(uri);
        
        Response response =webTarget.path("api/v1/hello").request().get();
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyPassword () {
        client.register(HttpAuthenticationFeature.basic("username", new String()));        
        WebTarget webTarget = client.target(uri);
        
        Response response =webTarget.path("api/v1/hello").request().get();
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyUsernameAndPassword () {
        client.register(HttpAuthenticationFeature.basic(new String(), ""));        
        WebTarget webTarget = client.target(uri);
        
        Response response =webTarget.path("api/v1/hello").request().get();
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
}
