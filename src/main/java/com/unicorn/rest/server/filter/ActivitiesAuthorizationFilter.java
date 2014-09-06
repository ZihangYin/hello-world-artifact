package com.unicorn.rest.server.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unicorn.rest.activities.exception.AccessDeniedException;
import com.unicorn.rest.activities.exception.BadRequestException;
import com.unicorn.rest.activities.exception.InternalServerErrorException;
import com.unicorn.rest.activities.exception.MissingAuthorizationException;
import com.unicorn.rest.activities.exception.UnrecognizedAuthorizationMethodException;
import com.unicorn.rest.repository.AuthenticationTokenRepository;
import com.unicorn.rest.server.filter.model.AuthenticationMethod;
import com.unicorn.rest.server.filter.utils.BearerAuthorizer;

//@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class ActivitiesAuthorizationFilter implements ContainerRequestFilter {
    private static final Logger LOG = LogManager.getLogger(ActivitiesAuthorizationFilter.class);

    @Inject
    private AuthenticationTokenRepository tokenRepository;
    
    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (StringUtils.isBlank(authorizationHeader)) {
            throw new MissingAuthorizationException();
        }
        if (!authorizationHeader.startsWith(AuthenticationMethod.BEARER_AUTHENTICATION.toString())) {
            throw new UnrecognizedAuthorizationMethodException();
        }
        String authorizationCode = authorizationHeader.replaceFirst(AuthenticationMethod.BEARER_AUTHENTICATION.toString(), "");
        try {
            BearerAuthorizer.authenticate(authorizationCode, tokenRepository);
        } catch (AccessDeniedException error) {
            LOG.info("Failed while attempting to fulfill unauthorized request: " + error.getMessage());
            throw error;
        } catch (Exception error) {
            LOG.error("Failed while attempting to fulfill authorization due to internal request ", error);
            throw new InternalServerErrorException(error);
        }
        try {
            
        } catch (BadRequestException badRequest) {
            
            
        }
    }
}
