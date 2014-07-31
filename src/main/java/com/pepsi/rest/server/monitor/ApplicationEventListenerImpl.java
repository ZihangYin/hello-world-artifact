package com.pepsi.rest.server.monitor;

import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

@Provider
public class ApplicationEventListenerImpl implements ApplicationEventListener {
    private static final Logger LOG = LogManager.getLogger(ApplicationEventListenerImpl.class);
    
    private AtomicLong maxRequestId = new AtomicLong(0); 
    private long initializationStartingTime;        
    
    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_START:
                initializationStartingTime = System.currentTimeMillis(); 
                break;
                
            case INITIALIZATION_FINISHED:
                LOG.info("Application {} was initialized in {} ms. ", 
                        event.getResourceConfig().getApplicationName(),
                        System.currentTimeMillis() - initializationStartingTime);
                break;
                
            case DESTROY_FINISHED:
                LOG.info("Application {} was destroyed. ", 
                    event.getResourceConfig().getApplicationName());
                break;
        default:
            break;
        }
    }
 
    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {        
        
        /**
         * Return the listener instance that will handle this request.
         * 
         * Note: We currently do not handle the case when maxRequestId is overflow
         * since it is very unlikely.
         */
        return new RequestEventListenerImpl(maxRequestId.incrementAndGet());
    }
}
