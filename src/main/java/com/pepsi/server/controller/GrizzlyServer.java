package com.pepsi.server.controller;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


public class GrizzlyServer {
        
    // This method is protected for unit test.
    protected static HttpServer startGrizzlyWebServer(URI uri) {
                
        try {                       
            // create a resource config that scans for JAX-RS resources and providers
            // in com.pepsi.api package
            ResourceConfig resourceConfig = new ResourceConfig().packages("com.pepsi.api");

            // create and start a new instance of grizzly http server
            // exposing the Jersey application at uri
            return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);
            
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    // This method is protected for unit test.
    protected static void shutdownGrizzlyWebServer(HttpServer grizzlyWebServer) {
        if (grizzlyWebServer != null && grizzlyWebServer.isStarted()) {
            grizzlyWebServer.shutdownNow();
        }        
    }

    public static void main(String[] args) {        
        
        URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        HttpServer grizzlyWebServer = null;
        try {
            grizzlyWebServer = startGrizzlyWebServer(uri);
            System.in.read();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            
        } finally {
            shutdownGrizzlyWebServer(grizzlyWebServer);
            
        }
    }

}
