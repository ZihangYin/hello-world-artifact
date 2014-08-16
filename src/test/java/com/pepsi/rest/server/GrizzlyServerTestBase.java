package com.pepsi.rest.server;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.junit.AfterClass;

import com.pepsi.rest.commons.JSONObjectMapperImpl;
import com.pepsi.rest.server.GrizzlyServerOrchestrator;

public class GrizzlyServerTestBase {

    private static final String CLIENT_TRUSTORE_FILE_PROPERTY = "CLIENT_TRUSTORE_FILE";
    private static final String CLIENT_TRUSTORE_PASSWORD_PROPERTY = "CLIENT_TRUSTORE_PASSWORD";
    private static final String HTTP_SERVER_PROPERTIES_FILE = "test-http-server.properties";
    private static final String HTTPS_SERVER_PROPERTIES_FILE = "test-https-server.properties";
    
    protected static HttpServer grizzlyWebServer;
    protected static URI uri;
    
    protected static void setUpHttpWebServer() throws Exception {
        String serverPropertiesFile = HTTP_SERVER_PROPERTIES_FILE;
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);
        uri = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);
    }
    
    protected static void setUpHttpWebServlet() throws Exception {
        String serverPropertiesFile = HTTP_SERVER_PROPERTIES_FILE;
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);
        uri = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServlet(serverPropertiesFile);
    }
    
    protected static Client getHttpClient() {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        clientConfig.register(JSONObjectMapperImpl.class);
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
        return client;
    }
    
    protected static void setUpHttpsWebServer() throws Exception {
        String serverPropertiesFile = HTTPS_SERVER_PROPERTIES_FILE;
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);
        uri = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);
    }
    
    protected static void setUpHttpsWebServlet() throws Exception {
        String serverPropertiesFile = HTTPS_SERVER_PROPERTIES_FILE;
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);
        uri = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServlet(serverPropertiesFile);
    }
    
    protected static Client getHttpsClient () throws IOException {
        PropertiesParser serverPropertiesParser = new PropertiesParser(HTTPS_SERVER_PROPERTIES_FILE);
        return getHttpsClient(serverPropertiesParser.getProperty(GrizzlyServerOrchestrator.HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY));
    }
    
    private static Client getHttpsClient (String certificatePropertiesFile) throws IOException {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        clientConfig.register(JSONObjectMapperImpl.class);
        PropertiesParser certificatePropertiesParser = new PropertiesParser(certificatePropertiesFile);
        SslConfigurator sslConfigurator = SslConfigurator.newInstance()
                .trustStoreFile(certificatePropertiesParser.getProperty(CLIENT_TRUSTORE_FILE_PROPERTY))
                .trustStorePassword(certificatePropertiesParser.getProperty(CLIENT_TRUSTORE_PASSWORD_PROPERTY));
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig)
                .sslContext(sslConfigurator.createSSLContext()).build();
        return client;
    }
    
    @AfterClass
    public static void tearDownWebServer() throws Exception {
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
    }
}
