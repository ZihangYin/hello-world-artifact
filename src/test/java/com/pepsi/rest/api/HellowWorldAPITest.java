package com.pepsi.rest.api;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldAPITest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    
    @Test
    public void testSayHello() {
        String responseMsg = webTarget.path("api").request().get(String.class);
        assertEquals("Hello World!", responseMsg);
    }
}
