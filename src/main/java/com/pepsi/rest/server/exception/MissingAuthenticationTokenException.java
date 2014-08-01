package com.pepsi.rest.server.exception;

public class MissingAuthenticationTokenException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2770428583760909772L;
    
    public MissingAuthenticationTokenException(String errMsg) {
        super(errMsg);
    }
}
