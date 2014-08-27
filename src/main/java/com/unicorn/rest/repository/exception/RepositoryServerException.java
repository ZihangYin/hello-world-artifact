package com.unicorn.rest.repository.exception;

public class RepositoryServerException extends Exception {

    private static final long serialVersionUID = 6883344758645289772L;

    public RepositoryServerException(String errMsg, Exception cause) {
        super(errMsg, cause);
    }
}
