package com.pepsi.rest.server.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class ActivityFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(ActivityFilter.class);
    
    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        // TODO LOG Request Information
        LOG.debug("RequstUserAgent: {}", requestContext.getHeaders().get("user-agent")); 
    }
}
