package com.pepsi.rest.activity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldActivityTest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    private static WebTarget webTarget;
    
    @BeforeClass
    public static void setUpWebServer() throws Exception {
        setUpHttpsWebServer();
        
        Client client = getHttpsClient();
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        webTarget = client.target(uri);
    }
    
    @Test
    public void testSayHelloHappyCase() {        
        
        Response response = webTarget.path("api/v1/hello").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));
    }
}
