package com.unicorn.rest.server.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unicorn.rest.activities.exception.BadRequestException;
import com.unicorn.rest.server.filter.utils.Authorizer;
import com.unicorn.rest.server.filter.utils.Authorizer.AuthenticationMethod;

//@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class ActivitiesAuthorizationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(ActivitiesAuthorizationFilter.class);
    public static final String OAUTH_AUTHENTICATION = "Bearer ";

    @Context
    private HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        try {
            Authorizer.validateAuthorizationHeader(authorizationHeader, AuthenticationMethod.BEARER_AUTHENTICATION);
        } catch (BadRequestException badRequest) {
            LOG.info("Failed while attempting to fulfill unauthorized request: " + badRequest.getMessage());
        }
    }
}
