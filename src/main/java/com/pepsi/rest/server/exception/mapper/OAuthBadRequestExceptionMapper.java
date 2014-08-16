package com.pepsi.rest.server.exception.mapper;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pepsi.rest.server.exception.OAuthBadRequestException;

@Provider
public class OAuthBadRequestExceptionMapper implements ExceptionMapper<OAuthBadRequestException> {

    @Context
    private HttpHeaders headers;
    
    @Override
    public Response toResponse(OAuthBadRequestException ex) {
        return Response.status(Status.BAD_REQUEST)
                .type(headers.getMediaType())
                .entity(ex.getOauthErrorResponse())
                .build();
    }
}
