package com.pepsi.rest.activities.exception;

public class AccessDeniedException extends BadRequestException {

    private static final long serialVersionUID = -1602889871627111884L;
    private static final String ERR_MSG = "[access_deny] Authorization failed due to missing, invalid or malformed principal and/or credential.";
    
    public AccessDeniedException() {
        super(ERR_MSG);
    }
}
