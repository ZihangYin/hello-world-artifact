package com.pepsi.rest.activity;
import org.junit.Test;

import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;

public class HellowWorldActivityTest extends GrizzlyServerTestBase {


    /**
     * Test to see that the message "Hello World!" is sent in the response.
     */
    
    @Test
    public void testSayHello() {
        String responseMsg = webTarget.path("api/hello").request().get(String.class);
        assertEquals("Hello World!", responseMsg);
    }
}
