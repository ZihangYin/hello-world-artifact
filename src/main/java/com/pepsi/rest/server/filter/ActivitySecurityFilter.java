package com.pepsi.rest.server.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

import com.pepsi.rest.server.exception.MissingAuthenticationTokenException;

@Provider
@PreMatching
@Priority(Integer.MIN_VALUE)
public class ActivitySecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext)
            throws IOException {

        String authentication = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authentication == null) {
            throw new MissingAuthenticationTokenException("Authentication Token is missing.");
        }
        
        if (authentication.startsWith("Basic") || authentication.startsWith("basic")) {
            //Replacing "Basic THE_BASE_64" to "THE_BASE_64" directly
            authentication = authentication.replaceFirst("[B|b]asic ", "");

            //Decode the Base64 into byte[]
            String[] authenticationInfo = Base64.decodeAsString(authentication).split(":");

            //If the decode fails in any case
            if (authenticationInfo.length < 2) {
                throw new MissingAuthenticationTokenException("Authentication Token is missing: Missing username and/or password.");
            }

            String username = authenticationInfo[0];
            String password = authenticationInfo[1];

            if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
                throw new MissingAuthenticationTokenException("Authentication Token is missing: Missing username and/or password.");
            }

            //TODO: authentication and possibly authorization
        }
    }
}
