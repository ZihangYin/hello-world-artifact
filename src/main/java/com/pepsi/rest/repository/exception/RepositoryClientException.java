package com.pepsi.rest.repository.exception;

public class RepositoryClientException extends Exception {

    private static final long serialVersionUID = -2085589214413487861L;
    
    public RepositoryClientException(String errMsg) {
        super(errMsg);
    }
}
