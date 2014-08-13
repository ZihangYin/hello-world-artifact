package com.pepsi.rest.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.pepsi.rest.constant.WebServiceConstants;

public class GrizzlyServerOrchestrator {

    private static final String SERVER_PROPERTIES_FILE = "server.properties";
    
    protected static final String HTTP_BASE_URL_PROPERTY = "HTTP_BASE_URL";
    protected static final String HTTP_PORT_PROPERTY = "HTTP_PORT";
    protected static final String HTTPS_BASE_URL_PROPERTY = "HTTPS_BASE_URL";
    protected static final String HTTPS_PORT_PROPERTY = "HTTPS_PORT";
    
    protected static final String HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY = "HTTPS_CERTIFICATE_PROPERTIES_FILE";
    protected static final String SERVER_KEYSTORE_FILE_PROPERTY = "SERVER_KEYSTORE_FILE";
    protected static final String SERVER_KEYSTORE_PASSWORD_PROPERTY = "SERVER_KEYSTORE_PASSWORD";
    protected static final String SERVER_TRUSTORE_FILE_PROPERTY = "SERVER_TRUSTORE_FILE";
    protected static final String SERVER_TRUSTORE_PASSWORD_PROPERTY = "SERVER_TRUSTORE_PASSWORD";
    
    private static volatile boolean terminate = false;

    public static void main(String[] args) throws IOException {
        
//        // Grizzly uses JUL and this prevents performance impact of bridging to logback
//        SLF4JBridgeHandler.removeHandlersForRootLogger();
//        SLF4JBridgeHandler.install();
        
        HttpServer grizzlyWebServer = null;
        try {
            printWithTimestamp(" [INFO] Starting Grizzly Server...");

            try {
                grizzlyWebServer = startGrizzlyWebServer(SERVER_PROPERTIES_FILE);
                printWithTimestamp(" [INFO] Grizzly Server Started");

            } catch(IllegalArgumentException iae) {
                printWithTimestamp(String.format(" [ERROR] %s", iae.getMessage()));
                return;
            } catch (RuntimeException re) {
                printWithTimestamp(String.format(" [ERROR] %s: %s", re.getMessage(), re.getCause()));
                return;
            }

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    terminate = true;
                }
            }));

            while(!terminate){}

        } finally {
            printWithTimestamp(" [INFO] Stopping Grizzly Server...");
            shutdownGrizzlyWebServer(grizzlyWebServer);
        }
    }

    public static HttpServer startGrizzlyWebServer(String serverPropertyFile) {
        HttpServer grizzlyWebServer = createGrizzlyWebServer(serverPropertyFile);
        startGrizzlyWebServer(grizzlyWebServer);
        return grizzlyWebServer;
    }
    
    public static HttpServer startGrizzlyWebServlet(String serverPropertyFile) {
        HttpServer grizzlyWebServer = createGrizzlyWebServer(serverPropertyFile);
        startGrizzlyWebServlet(grizzlyWebServer);
        return grizzlyWebServer;
    }

    public static void shutdownGrizzlyWebServer(HttpServer grizzlyWebServer) {
        if (grizzlyWebServer != null && grizzlyWebServer.isStarted()) {            
            GrizzlyFuture<HttpServer> future = grizzlyWebServer.shutdown();
            while (!future.isDone()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignore){}
            }            
        }      
    }
    
    protected static void startGrizzlyWebServer(HttpServer grizzlyWebServer) {
        try {
            // Start the server.
            grizzlyWebServer.start();
        } catch (IOException ioe) {
            grizzlyWebServer.shutdownNow();
            throw new RuntimeException("Grizzly Server failed while attempting to start" , ioe);
        }
    }
    
    protected static void startGrizzlyWebServlet(HttpServer grizzlyWebServer) {
        WebappContext webappContext = new WebappContext("GRIZZLY Web Server WebappContext");
        ServletRegistration servletRegistration = webappContext.addServlet("JerseyServletContainer", ServletContainer.class);
        servletRegistration.setInitParameter("jersey.config.server.provider.packages", WebServiceConstants.ROOT_PACKAGE);
        servletRegistration.addMapping("/*");
        webappContext.deploy(grizzlyWebServer);
        
        startGrizzlyWebServer(grizzlyWebServer);
    }

    /**
     * @param serverPropertiesParser @Nonnull
     * @param baseURIProperty @Nonnull
     * @param portProperty @Nonnull
     * @return NULL if given properties does not exist; 
     * @throws IllegalArgumentException if port is not an integer
     */
    protected static URI buildGrizzlyServerURI(PropertiesParser serverPropertiesParser, String baseURIProperty, String portProperty) {
        try {
            String baseURI = serverPropertiesParser.getProperty(baseURIProperty);
            String port = serverPropertiesParser.getProperty(portProperty);

            if (baseURI != null && port != null) {
                return UriBuilder.fromUri(baseURI).port(Integer.parseInt(port)).build();
            } else {
                return null;
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(String.format("Grizzly Server failed while attempting to parse port property %s: %s", portProperty, nfe));
        }
    }

    /**
     * @return SSLEngineConfigurator
     * @throws RuntimeException if failed to load CERTIFICATE_PROPERTIES_FILE 
     */
    private static SSLEngineConfigurator buildSSLEngineConfigurator(String httpsCertificatePropertyFile) {
        if (httpsCertificatePropertyFile == null || httpsCertificatePropertyFile.isEmpty()) {
            throw new IllegalArgumentException("Grizzly Server failed while attempting to get https certificate property");
        }
        
        try {
            PropertiesParser certificatePropertiesParser = new PropertiesParser(httpsCertificatePropertyFile);

            boolean clientAuth = false;
            // Grizzly SSL configuration
            SSLContextConfigurator sslContext = new SSLContextConfigurator();
            
            String keyStoreServerFile = certificatePropertiesParser.getProperty(SERVER_KEYSTORE_FILE_PROPERTY);
            String keyStoreServerPassword = certificatePropertiesParser.getProperty(SERVER_KEYSTORE_PASSWORD_PROPERTY);
            if (keyStoreServerFile == null || keyStoreServerPassword == null) {
                throw new IllegalArgumentException("Grizzly Server failed while attempting to get server keystore file");
            }
            
            if (!new File(keyStoreServerFile).exists()) {
                throw new IllegalArgumentException( String.format("Grizzly Server failed while attempting to load server keystore file %s", keyStoreServerFile));
            }
            
            sslContext.setKeyStoreFile(keyStoreServerFile); 
            sslContext.setKeyStorePass(keyStoreServerPassword);
            // contains client certificate
            String trustStoreFile = certificatePropertiesParser.getProperty(SERVER_TRUSTORE_FILE_PROPERTY);
            String trustStorePassword = certificatePropertiesParser.getProperty(SERVER_TRUSTORE_PASSWORD_PROPERTY);

            if (trustStoreFile != null && trustStorePassword != null) {
                if (!new File(trustStoreFile).exists()) {
                    throw new IllegalArgumentException( String.format("Grizzly Server failed while attempting to load server truststore file %s", trustStoreFile));
                }
                
                sslContext.setTrustStoreFile(trustStoreFile);
                sslContext.setTrustStorePass(trustStorePassword);
                clientAuth = true;
            }
            
            return new SSLEngineConfigurator(sslContext, false, clientAuth, clientAuth);
        } catch (IOException ioe) {
            throw new RuntimeException( String.format("Grizzly Server failed while attempting to load %s", httpsCertificatePropertyFile), ioe);
        }
    }
    
    // This method is protected for unit test.
    protected static HttpServer createGrizzlyWebServer(String serverPropertyFile) {
        try {
            PropertiesParser serverPropertiesParser = new PropertiesParser(serverPropertyFile);
        
            URI httpURI = buildGrizzlyServerURI(serverPropertiesParser, HTTP_BASE_URL_PROPERTY, HTTP_PORT_PROPERTY);
            URI httpsURI = buildGrizzlyServerURI(serverPropertiesParser, HTTPS_BASE_URL_PROPERTY, HTTPS_PORT_PROPERTY);
            if (httpURI == null && httpsURI == null) {
                throw new IllegalArgumentException("Grizzly Server failed while attempting to load URI and port: No URI and port provided");
            }
            
            HttpServer grizzlyWebServer= new HttpServer();
            /*
             * create a resource config that scans for JAX-RS resources and providers under ebServiceConstants.ROOT_PACKAGE
             * Note: All the API and filter should under this ROOT_PACKAGE. Otherwise, we will get 404 Not Found and filters will not get triggered.
             */
            ResourceConfig resourceConfig = new ResourceConfig().packages(WebServiceConstants.ROOT_PACKAGE).setApplicationName(WebServiceConstants.APPLICATION_NAME);
            ServerConfiguration serverConfiguration = grizzlyWebServer.getServerConfiguration();
            GrizzlyHttpContainer grizzlyHttpHandler = ContainerFactory.createContainer(GrizzlyHttpContainer.class, resourceConfig);
            serverConfiguration.setPassTraceRequest(true);

            if (httpURI != null) {
                NetworkListener httpListener = new NetworkListener("GRIZZLY-HTTP", httpURI.getHost(), httpURI.getPort());
                grizzlyWebServer.addListener(httpListener);
                serverConfiguration.addHttpHandler(grizzlyHttpHandler, httpURI.getPath());
            }   
            
            if (httpsURI != null) {
                NetworkListener httpsListener = new NetworkListener("GRIZZLY-HTTPS", httpsURI.getHost(), httpsURI.getPort());
                httpsListener.setSecure(true);
                httpsListener.setSSLEngineConfig(buildSSLEngineConfigurator(serverPropertiesParser.getProperty(HTTPS_CERTIFICATE_PROPERTIES_FILE_PROPERTY)));
                grizzlyWebServer.addListener(httpsListener);
                serverConfiguration.addHttpHandler(grizzlyHttpHandler, httpsURI.getPath());
            }

            return grizzlyWebServer;
        } catch (IOException ioe) {
            throw new RuntimeException( String.format("Grizzly Server failed while attempting to load %s", serverPropertyFile), ioe);
        }
    }

    private static void printWithTimestamp(String message) {
        System.out.println(new Date().toString() + message); 
    }

}
