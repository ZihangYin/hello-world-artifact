package com.pepsi.rest.server.exception;

public class MissingAuthenticationTokenException extends RuntimeException {

    static final long serialVersionUID = -2770428583760909772L;
    
    public MissingAuthenticationTokenException(String errMsg) {
        super(errMsg);
    }
}
