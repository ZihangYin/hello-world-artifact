package com.pepsi.api;
import org.junit.Test;

import com.pepsi.server.controller.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldTextAPITest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    
    @Test
    public void testHelloWorld() {
        String responseMsg = webTarget.path("api").request().get(String.class);
        assertEquals("Hello World!", responseMsg);
    }
}
