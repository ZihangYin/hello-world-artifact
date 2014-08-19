package com.pepsi.rest.server;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;

import com.pepsi.rest.server.GrizzlyServerOrchestrator;
import com.pepsi.rest.utils.JSONObjectMapperImpl;

public class GrizzlyServerIntegrationTestBase {

    private static final String CLIENT_TRUSTORE_FILE_PROPERTY = "CLIENT_TRUSTORE_FILE";
    private static final String CLIENT_TRUSTORE_PASSWORD_PROPERTY = "CLIENT_TRUSTORE_PASSWORD";
    public static final String DEFAULT_HTTPS_SERVER_PROPERTIES_FILE = "test-https-server.properties";
    
    protected static HttpServer grizzlyWebServer;
    protected static Client  client;
    protected static URI uri;
    protected static String overrideServerPropertiesFile;
    
    protected static void setUpHttpsWebServer() throws Exception {
        setUpHttpsWebServer(null, null);
    }
    
    protected static void setUpHttpsWebServer(String serverPropertiesFile) throws Exception {
        setUpHttpsWebServer(serverPropertiesFile, null);
    }
    
    protected static void setUpHttpsWebServer(ResourceConfig resourceConfig) throws Exception {
        setUpHttpsWebServer(null, resourceConfig);
    }

    protected static void setUpHttpsWebServer(String serverPropertiesFile, ResourceConfig resourceConfig) throws Exception {
        overrideServerPropertiesFile = serverPropertiesFile == null ? DEFAULT_HTTPS_SERVER_PROPERTIES_FILE : serverPropertiesFile;
        ResourceConfig overrideResourceConfig = resourceConfig == null ? GrizzlyServerOrchestrator.createResourceConfig() : resourceConfig;
        
        PropertiesParser serverPropertiesParser = new PropertiesParser(overrideServerPropertiesFile);
        uri = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);
        
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(overrideServerPropertiesFile, overrideResourceConfig);
        client = getHttpsClient ();
    }
    
    private static Client getHttpsClient () throws IOException {
        PropertiesParser serverPropertiesParser = new PropertiesParser(overrideServerPropertiesFile);
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        clientConfig.register(JSONObjectMapperImpl.class);
        PropertiesParser certificatePropertiesParser = new PropertiesParser(
                serverPropertiesParser.getProperty(GrizzlyServerOrchestrator.HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY));
        
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
