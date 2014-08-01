package com.pepsi.rest.server;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.pepsi.rest.constant.WebServiceConstants;


public class GrizzlyServer {
    private static final Logger LOG = LogManager.getLogger(GrizzlyServer.class);
    
    // This method is protected for unit test.
    protected static HttpServer startGrizzlyWebServer(URI uri) {
                
        try {
            /*
             * create a resource config that scans for JAX-RS resources and providers under ebServiceConstants.ROOT_PACKAGE
             * 
             * Note: 
             * All the API and filter should under this ROOT_PACKAGE. Otherwise, we will get 404 Not Found and filters will not get triggered.
             */
            ResourceConfig resourceConfig = new ResourceConfig().packages(WebServiceConstants.ROOT_PACKAGE).setApplicationName("HelloWorld Application");
            // create and start a new instance of grizzly http server
            // exposing the Jersey application at uri                     
            return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);
            
        } catch(Exception ex) {
            LOG.fatal("Grizzly server failed to start up: {}." + ex.getMessage());
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
        } finally {
            shutdownGrizzlyWebServer(grizzlyWebServer);
            
        }
    }
}
