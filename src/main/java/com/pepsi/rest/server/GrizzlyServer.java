package com.pepsi.rest.server;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.pepsi.rest.constant.WebServiceConstants;


public class GrizzlyServer {
    
    private static final String SERVER_PROPERTIES_FILE = "server.properties";
    
    private static volatile boolean keepRunning = true;

    public static void main(String[] args) throws IOException {        
        
        HttpServer grizzlyWebServer = null;
        try {
            prettyPrint(" [INFO] Starting Grizzly Server ....");
            
            try {
                URI grizzlyServerURI = buildGrizzlyServerURI();
                grizzlyWebServer = startGrizzlyWebServer(grizzlyServerURI);
                prettyPrint(" [INFO] Grizzly Server Started");
                
            } catch(IOException ioe) {
                prettyPrint(" [ERROR] Grizzly Server failed while attempting to build URI: " + ioe);
            }
           
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    keepRunning = false;
                }
            }));
            
            while(keepRunning){}
            
        } finally {
            prettyPrint(" [INFO] Stopping Grizzly Server ....");
            shutdownGrizzlyWebServer(grizzlyWebServer);
            try {
                Thread.currentThread().join();
            } catch (InterruptedException ignore) {}
        }
    }
    
    // This method is protected for unit test.
    protected static HttpServer startGrizzlyWebServer(URI uri) {

        /*
         * create a resource config that scans for JAX-RS resources and providers under ebServiceConstants.ROOT_PACKAGE
         * 
         * Note: 
         * All the API and filter should under this ROOT_PACKAGE. Otherwise, we will get 404 Not Found and filters will not get triggered.
         */
        ResourceConfig resourceConfig = new ResourceConfig().packages(WebServiceConstants.ROOT_PACKAGE).setApplicationName(WebServiceConstants.APPLICATION_NAME);
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at uri                     
        return GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);       
    }

    // This method is protected for unit test.
    protected static void shutdownGrizzlyWebServer(HttpServer grizzlyWebServer) {
        if (grizzlyWebServer != null && grizzlyWebServer.isStarted()) {            
            GrizzlyFuture<HttpServer> future = grizzlyWebServer.shutdown();
            while (!future.isDone()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore){}
            }            
        }      
    }
    
    private static URI buildGrizzlyServerURI() throws IOException {
        PropertiesParser serverPropertiesParser = new PropertiesParser(SERVER_PROPERTIES_FILE);
        
        String baseURI = serverPropertiesParser.getProperty("HTTP_BASE_URL");
        int port = Integer.parseInt(serverPropertiesParser.getProperty("HTTP_PORT"));
        
        return UriBuilder.fromUri(baseURI).port(port).build();
    }
    
    private static void prettyPrint(String message) {
        System.out.println(new Date().toString() + message); 
    }
}
