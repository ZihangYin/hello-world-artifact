package com.pepsi.rest.repository.exception;

public class RepositoryMismatchException extends RepositoryClientException{

    private static final long serialVersionUID = 6919636480095144944L;
    private static final String ERROR_MESSAGE = "Repository retrieved does not match the pattern of the expected type";
    
    public RepositoryMismatchException() {
        super(ERROR_MESSAGE);
    }

}
