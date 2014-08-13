package com.pepsi.rest.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrizzlyServerOrchestratorTest {

    private static final String CLIENT_TRUSTORE_FILE_PROPERTY = "CLIENT_TRUSTORE_FILE";
    private static final String CLIENT_TRUSTORE_PASSWORD_PROPERTY = "CLIENT_TRUSTORE_PASSWORD";
    private static HttpServer grizzlyWebServer;
    private static Client client;
    
    @BeforeClass
    public static void setupClient() throws Exception {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());

        PropertiesParser certificatePropertiesParser = new PropertiesParser("test-https-certificates.properties");
        SslConfigurator sslConfigurator = SslConfigurator.newInstance()
                .trustStoreFile(certificatePropertiesParser.getProperty(CLIENT_TRUSTORE_FILE_PROPERTY))
                .trustStorePassword(certificatePropertiesParser.getProperty(CLIENT_TRUSTORE_PASSWORD_PROPERTY));
        client = ClientBuilder.newBuilder().withConfig(clientConfig).sslContext(sslConfigurator.createSSLContext()).build();
        client.register(HttpAuthenticationFeature.basic("username", "password"));
    }
    
    @After
    public void tearDown() throws Exception {
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
    }

    @Test
    public void testMissingServerPropertiesFile() {
        String serverPropertyFile = "server-does-not-exist.properties";
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (RuntimeException re) {
            assertEquals(String.format("Grizzly Server failed while attempting to load %s", serverPropertyFile), re.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testEmptyServerPropertiesFile() throws IOException {
        String serverPropertyFile = "test-empty-server.properties";       
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (IllegalArgumentException iae) {
            assertEquals("Grizzly Server failed while attempting to load URI and port: No URI and port provided", iae.getMessage()); 
            return;
        }
        fail();
    }

    @Test
    public void testMissingCertificateProperties() {
        String serverPropertyFile = "test-missing-certificate-property-server.properties";
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (IllegalArgumentException iae) {
            assertEquals(String.format("Grizzly Server failed while attempting to get https certificate property"), iae.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testMissingCertificatePropertiesFile() throws IOException {
        String serverPropertyFile = "test-certificate-file-does-not-exist-server.properties";       
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (RuntimeException re) {
            PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertyFile);
            assertEquals(String.format("Grizzly Server failed while attempting to load %s", 
                    serverPropertiesParser.getProperty(GrizzlyServerOrchestrator.HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY)), 
                    re.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testBadPortServerPropertiesFile() throws IOException {
        String serverPropertyFile = "test-bad-port-server.properties";       
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (IllegalArgumentException iae) {
            return;
        }
        fail();
    }
    
    @Test
    public void testMissingKeyStoreProperties() throws IOException {
        String serverPropertyFile = "test-missing-keystore-property-server.properties";       
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (IllegalArgumentException iae) {
            assertEquals("Grizzly Server failed while attempting to get server keystore file", iae.getMessage());
            return;
        }
        fail();
    }
    
    @Test
    public void testMissingKeyStorePropertiesFile() throws IOException {
        String serverPropertyFile = "test-keystore-file-does-not-exist-server.properties";       
        try {
            grizzlyWebServer = GrizzlyServerOrchestrator.createGrizzlyWebServer(serverPropertyFile);
        } catch (IllegalArgumentException iae) {
            PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertyFile);
            PropertiesParser certificatePropertiesParser = new PropertiesParser(
                    serverPropertiesParser.getProperty(GrizzlyServerOrchestrator.HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY));

            assertEquals(String.format("Grizzly Server failed while attempting to load server keystore file %s", 
                    certificatePropertiesParser.getProperty(GrizzlyServerOrchestrator.SERVER_KEYSTORE_FILE_PROPERTY)), iae.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void testWithBasicAuthOverHttpHappyCase() throws IOException {
        String serverPropertiesFile = "test-http-server.properties";
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);

        URI httpURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        URI httpsURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);

        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);

        assertHelloWorldResourceHappyCase(client, httpURI);
        assertNull(httpsURI);
        assertHelloWorldResourceFailedCase(client, "https://localhost:8444");
        
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServlet(serverPropertiesFile);
        
        assertHelloWorldResourceHappyCase(client, httpURI);
        assertNull(httpsURI);
        assertHelloWorldResourceFailedCase(client, "https://localhost:8444");
        
    }

    @Test
    public void testSSLWithBasicAuthOverHttpsHappyCase() throws IOException {

        String serverPropertiesFile = "test-https-server.properties";
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);

        URI httpURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        URI httpsURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);

        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);

        assertHelloWorldResourceHappyCase(client, httpsURI);
        assertNull(httpURI);
        assertHelloWorldResourceFailedCase(client, "http://localhost:8081");
        
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServlet(serverPropertiesFile);
        
        assertHelloWorldResourceHappyCase(client, httpsURI);
        assertNull(httpURI);
        assertHelloWorldResourceFailedCase(client, "http://localhost:8081");
    }

    @Test
    public void testSSLWithBasicAuthOverHttpAndHttpsHappyCase() throws IOException {
        String serverPropertiesFile = "test-http-https-server.properties";
        PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertiesFile);

        URI httpURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTP_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTP_PORT_PROPERTY);
        URI httpsURI = GrizzlyServerOrchestrator.buildGrizzlyServerURI(serverPropertiesParser, 
                GrizzlyServerOrchestrator.HTTPS_BASE_URL_PROPERTY, GrizzlyServerOrchestrator.HTTPS_PORT_PROPERTY);

        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServer(serverPropertiesFile);

        assertHelloWorldResourceHappyCase(client, httpURI);
        assertHelloWorldResourceHappyCase(client, httpsURI);
        
        GrizzlyServerOrchestrator.shutdownGrizzlyWebServer(grizzlyWebServer);
        grizzlyWebServer = GrizzlyServerOrchestrator.startGrizzlyWebServlet(serverPropertiesFile);
        
        assertHelloWorldResourceHappyCase(client, httpURI);
        assertHelloWorldResourceHappyCase(client, httpsURI);
    }
    
    private void assertHelloWorldResourceHappyCase(Client client, URI uri) {
        WebTarget webTarget = client.target(uri);
        Response response = webTarget.path("api/v1/hello").request().get();

        assertEquals(200, response.getStatus());
        assertEquals("Hello World", response.readEntity(String.class));
    }
    
    private void assertHelloWorldResourceFailedCase(Client client, String uri) {
        WebTarget webTarget = client.target(uri);
        try {
            webTarget.path("api/v1/hello").request().get();
        } catch (ProcessingException pe) {
            assertEquals("Connection refused", pe.getMessage());
        }
    }
}
