package com.pepsi.rest.activities.exception.mapper;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pepsi.rest.activities.exception.BadRequestException;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(BadRequestException badRequest) {

        ResponseBuilder responseBuilder = Response.status(Status.BAD_REQUEST)
                .type(headers.getMediaType());

        if (badRequest.getErrorResponse() != null) {
            responseBuilder.entity(badRequest.getErrorResponse());
        } else {
            responseBuilder.entity(badRequest.getMessage());
        }

        return responseBuilder.build();
    }
}
