package com.pepsi.rest.server;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.pepsi.rest.server.GrizzlyServerOrchestrator;

public class GrizzlyServerTestBase {

    protected static HttpServer grizzlyWebServer;
    protected static URI httpURI;
    
    @BeforeClass
    public static void setUpHttpWebServer() throws Exception {
        String serverPropertiesFile = "test-http-server.properties";
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);
        httpURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
    }
}
