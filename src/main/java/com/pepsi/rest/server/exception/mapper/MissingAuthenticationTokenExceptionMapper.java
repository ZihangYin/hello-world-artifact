package com.pepsi.rest.server.exception.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pepsi.rest.server.exception.MissingAuthenticationTokenException;

@Provider
public class MissingAuthenticationTokenExceptionMapper implements ExceptionMapper<MissingAuthenticationTokenException> {

    @Override
    public Response toResponse(MissingAuthenticationTokenException exception) {
        return Response.status(Status.UNAUTHORIZED)
                /** 
                 * We need to set the WWW-Authentication header.
                 * Otherwise, it will throw javax.ws.rs.ProcessingException: 401 response received, but no WWW-Authenticate header was present on client side.
                 */                
                .header("WWW-Authenticate", "Basic realm=HelloWorldApplication")
                .type(MediaType.TEXT_PLAIN_TYPE)
                .entity(exception.getMessage())
                .build();
    }
}
