package com.pepsi.rest.activities.exception;

public class MissingAuthorizationException extends BadRequestException {

    private static final long serialVersionUID = -6118015887270506647L;
    private static final String ERR_MSG = "[missing_authorization] Authorization failed due to no client authentication provided.";
    
    public MissingAuthorizationException() {
        super(ERR_MSG);
    }
}
