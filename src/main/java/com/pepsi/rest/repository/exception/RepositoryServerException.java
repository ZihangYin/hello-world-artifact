package com.pepsi.rest.repository.exception;

public class RepositoryServerException extends Exception {

    private static final long serialVersionUID = -2085589214413487861L;
    
    public RepositoryServerException(String errMsg, Exception cause) {
        super(errMsg, cause);
    }
}
