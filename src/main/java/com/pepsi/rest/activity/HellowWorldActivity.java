package com.pepsi.rest.activity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * TODO: Implement API call rate limiting.  
 */

@Path("api")
public class HellowWorldActivity {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    /*
     * Requiring version and media type in the URL instead of Accept header or customized header 
     * to ensure browser explorability of the resources across versions and media types.
     */
    @Path("/v1/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHello() {
        return Response.ok("Hello World").build();        
    }
}
