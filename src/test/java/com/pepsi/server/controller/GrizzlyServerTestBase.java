package com.pepsi.server.controller;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pepsi.server.controller.GrizzlyServer;

public class GrizzlyServerTestBase {

    protected static HttpServer grizzlyWebServer;
    protected static WebTarget webTarget;

    @BeforeClass
    public static void setUp() throws Exception {
        
        URI uri = UriBuilder.fromUri("http://localhost/").port(8081).build();
        // start the server
        grizzlyWebServer = GrizzlyServer.startGrizzlyWebServer(uri);
        // create the client
        Client client = ClientBuilder.newClient();
        webTarget = client.target(uri);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        GrizzlyServer.shutdownGrizzlyWebServer(grizzlyWebServer);
    }
}
