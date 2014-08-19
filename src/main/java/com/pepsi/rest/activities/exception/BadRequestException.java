package com.pepsi.rest.activities.exception;

import com.pepsi.rest.activity.model.ErrorResponse;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 3590068626650050213L;
    public static final String BAD_REQUEST = "Bad Request";
    
    protected ErrorResponse errorResponse;
    
    public BadRequestException(String errMsg) {
        super(errMsg);
    }
    
    public BadRequestException(ErrorResponse errorResponse) {
        super(errorResponse.getErrMsg());
        this.errorResponse = errorResponse;
    }
    
    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
