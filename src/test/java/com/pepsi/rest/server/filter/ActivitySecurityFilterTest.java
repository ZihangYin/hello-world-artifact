package com.pepsi.rest.server.filter;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

public class ActivitySecurityFilterTest extends GrizzlyServerTestBase {
    
    @Test
    public void testActivitySecurityFilterOverHttpHappyCase () {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();        
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        WebTarget webTarget = client.target(httpURI);
        
        Response response =webTarget.path("api/v1/hello").request().get();; 
        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));      
    }
   
    @Test
    public void testActivitySecurityFilterOverHttpMissingAuthentication () {
        
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(httpURI);
        
        Response response =webTarget.path("api/v1/hello").request().get();; 
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing.", response.readEntity(String.class));
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyUsername () {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();        
        client.register(HttpAuthenticationFeature.basic("", "password"));        
        WebTarget webTarget = client.target(httpURI);
        
        Response response =webTarget.path("api/v1/hello").request().get();; 
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyPassword () {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();        
        client.register(HttpAuthenticationFeature.basic("username", new String()));        
        WebTarget webTarget = client.target(httpURI);
        
        Response response =webTarget.path("api/v1/hello").request().get();; 
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
    
    @Test
    public void testActivitySecurityFilterOverHttpEmptyUsernameAndPassword () {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();        
        client.register(HttpAuthenticationFeature.basic(new String(), ""));        
        WebTarget webTarget = client.target(httpURI);
        
        Response response =webTarget.path("api/v1/hello").request().get();; 
        assertEquals(401, response.getStatus());
        assertEquals("Authentication Token is missing: Missing username and/or password.", response.readEntity(String.class));
        
    }
}
