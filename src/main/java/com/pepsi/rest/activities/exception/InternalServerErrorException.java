package com.pepsi.rest.activities.exception;

public class InternalServerErrorException extends RuntimeException {

    private static final long serialVersionUID = 4947832629382273858L;
    
    public static final String INTERNAL_FAILURE = "Internal Failure";
    public static final String ERR_MSG = "[internal_failure] The server encountered an internal error while attempting to fulfill the request";
    
    public InternalServerErrorException() {
        super(ERR_MSG);
    }
}
