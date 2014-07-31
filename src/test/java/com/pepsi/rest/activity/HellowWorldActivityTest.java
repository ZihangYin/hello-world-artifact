package com.pepsi.rest.activity;

import javax.ws.rs.core.Response;

import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldActivityTest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    
    @Test
    public void testSayHello() {        
        Response response = webTarget.path("api/hello").request().get();
        
        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));
    }
}
