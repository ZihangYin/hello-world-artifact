package com.pepsi.rest.server.monitor;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

//Comment out this event listener since we do not use it at this moment.
//Without @Provider, this event listener will not be registered.
//@Provider
public class ApplicationEventListenerImpl implements ApplicationEventListener {
    private static final Logger LOG = LogManager.getLogger(ApplicationEventListenerImpl.class);
    
    private final AtomicLong activityID = new AtomicLong(0);
    private long AppInitStartingTime;         
    
    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_START:
                AppInitStartingTime = System.currentTimeMillis(); 
                break;
                
            case INITIALIZATION_FINISHED:
                LOG.debug("Application {} was initialized in {} ms. ", 
                        event.getResourceConfig().getApplicationName(),
                        System.currentTimeMillis() - AppInitStartingTime);
                break;
                
            case DESTROY_FINISHED:
                LOG.debug("Application {} was destroyed. ", 
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
        return new AuditActivityEventListenerImpl(activityID.incrementAndGet());
    }
}
