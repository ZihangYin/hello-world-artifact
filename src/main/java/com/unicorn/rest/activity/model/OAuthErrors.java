package com.unicorn.rest.activity.model;

public class OAuthErrors {

    public enum OAuthErrCode {
        INVALID_REQUEST("invalid_request"),
        MISSING_PARAMETERS("missing_parameters"),
        UNAUTHENTICATED_CLIENT("unauthenticated_client"),
        INVALID_SCOPE("invalid_scope"),
        UNAUTHORIZED_CLIENT("unauthorized_client"),
        UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),
        INVALID_GRANT("invalid_grant");

        private String errCode;

        private OAuthErrCode(String errCode) {
            this.errCode = errCode;
        }

        @Override 
        public String toString() {
            return this.errCode;
        }
    }

    public enum OAuthErrDescFormatter {
        INVALID_REQUEST("The request is invalid due to missing or malformed parameters provided"),
        MISSING_PARAMETERS ("The request is missing required parameters: %s"),
        UNAUTHENTICATED_CLIENT ("The authentication failed on client %s due to unknown client or invalid signature"),
        INVALID_SCOPE ("The requested scope %s is invalid, unknown, malformed or exceeds the granted scope"),
        CLIENT_CREDENTIALS_NOT_PERMITTED ("The client %s has no permisssion for client credentials"),
        UNSUPPORTED_GRANT_TYPE ("The authorization grant type %s is not supported by the authorization server"),
        INVALID_GRANT_AUTHORIZATION_CODE ("The authorization code %s does not match any of our records"),
        INVALID_GRANT_REFRESH_TOKEN ("The refresh token %s does not match any of our records"),
        INVALID_GRANT_PASSWORD ("The authentication failed on user %s due to invalid user name or password");

        private String errDesc;

        private OAuthErrDescFormatter(String errDesc) {
            this.errDesc = errDesc;
        }

        @Override 
        public String toString() {
            return this.errDesc;
        }
    }
}
