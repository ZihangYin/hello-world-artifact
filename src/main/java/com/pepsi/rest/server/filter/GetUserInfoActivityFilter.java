package com.pepsi.rest.server.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetUserInfoActivityFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(GetUserInfoActivityFilter.class);
            
    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        LOG.debug("AcceptedMediaTypes: {}", requestContext.getAcceptableMediaTypes());    
    }
}

