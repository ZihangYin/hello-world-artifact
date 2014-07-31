package com.pepsi.rest.server.monitor;

import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

@Provider
public class RequestEventListenerImpl implements RequestEventListener {
    private static final Logger LOG = LogManager.getLogger(RequestEventListenerImpl.class);
    
    private final long requestId;
    private final long methodStartTime;
    
    public RequestEventListenerImpl(long requestId) {
        this.requestId = requestId;
        this.methodStartTime = System.currentTimeMillis();
    }
 
    @Override
    public void onEvent(RequestEvent event) {
        switch (event.getType()) {
            case RESOURCE_METHOD_START:
                LOG.debug("Resource method {} started for request with ID {}."
                    , event.getUriInfo().getMatchedResourceMethod()
                        .getHttpMethod(), requestId);
                break;
                
            case FINISHED:
                LOG.debug("Request with ID {} finished processing in {} ms. ", requestId
                    , (System.currentTimeMillis() - methodStartTime));
                break;
        default:
            break;
        }
    }

}
