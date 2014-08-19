package com.pepsi.rest.activities.exception;

public class UnrecognizedAuthorizationMethodException extends BadRequestException {

    private static final long serialVersionUID = 3515494136319922520L;
    private static final String ERR_MSG = "[unrecognized_authorization] This authentication method is either unexpected or unsupported by the server for this request";
    
    public UnrecognizedAuthorizationMethodException() {
        super(ERR_MSG);
    }
}
