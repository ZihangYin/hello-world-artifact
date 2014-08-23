package com.pepsi.rest.activities.exception;

public class UnrecognizedAuthorizationMethodException extends BadRequestException {

    private static final long serialVersionUID = 3515494136319922520L;
    private static final String ERROR_CODE = "unrecognized_authorization";
    private static final String ERROR_DESCRIPTION = "This authentication method is either unexpected or unsupported by the server for this request";
    
    public UnrecognizedAuthorizationMethodException() {
        super(ERROR_CODE, ERROR_DESCRIPTION);
    }
}
