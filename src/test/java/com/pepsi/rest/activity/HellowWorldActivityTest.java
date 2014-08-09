package com.pepsi.rest.activity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldActivityTest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    
    @Test
    public void testSayHelloHappyCase() {        
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        WebTarget webTarget = client.target(httpURI);
        
        Response response = webTarget.path("api/v1/hello").request().get();
        
        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));
    }
}
