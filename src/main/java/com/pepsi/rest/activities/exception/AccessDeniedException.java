package com.pepsi.rest.activities.exception;

public class AccessDeniedException extends BadRequestException {

    private static final long serialVersionUID = -1602889871627111884L;
    private static final String ERROR_CODE = "access_denied";
    private static final String ERROR_DESCRIPTION = "Authorization failed due to missing, invalid or malformed principal and/or credential.";
    public AccessDeniedException() {
        super(ERROR_CODE, ERROR_DESCRIPTION);
    }
}
