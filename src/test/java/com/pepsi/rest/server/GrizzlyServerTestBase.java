package com.pepsi.rest.server;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pepsi.rest.server.GrizzlyServer;

public class GrizzlyServerTestBase {

    protected static HttpServer grizzlyWebServer;
    protected static URI uri = UriBuilder.fromUri("http://localhost/").port(8081).build();
    
    @BeforeClass
    public static void setUp() throws Exception {       
        grizzlyWebServer = GrizzlyServer.startGrizzlyWebServer(uri);        
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        GrizzlyServer.shutdownGrizzlyWebServer(grizzlyWebServer);
    }
}
