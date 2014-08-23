package com.pepsi.rest.repository.exception;

public class DuplicateKeyException extends RepositoryClientException {
    
    private static final long serialVersionUID = -8622699679735630483L;
    private static final String ERROR_MESSAGE = "Object cannot be created because an object with the same key already exist";
    
    public DuplicateKeyException() {
        super(ERROR_MESSAGE);
    }
}
