package com.pepsi.rest.server;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.pepsi.rest.constant.WebServiceConstants;


public class GrizzlyServer {

    private static volatile boolean keepRunning = true;
    
    // This method is protected for unit test.
    protected static HttpServer startGrizzlyWebServer(URI uri) {

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
    }

    // This method is protected for unit test.
    protected static void shutdownGrizzlyWebServer(HttpServer grizzlyWebServer) {
        if (grizzlyWebServer != null && grizzlyWebServer.isStarted()) {
            GrizzlyFuture<HttpServer> future = grizzlyWebServer.shutdown(10000, TimeUnit.SECONDS);
            while (!future.isDone()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore){}
            }            
        }        
    }

    public static void main(String[] args) {        

        URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        HttpServer grizzlyWebServer = null;
        try {
            grizzlyWebServer = startGrizzlyWebServer(uri);
            System.out.println("Started server.");
            
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    keepRunning = false;
                }
            }, "shutdownHook"));
            
            while(keepRunning){}
            
        } finally {
            System.out.println("Stopping server.");
            shutdownGrizzlyWebServer(grizzlyWebServer);
        }
    }
}
