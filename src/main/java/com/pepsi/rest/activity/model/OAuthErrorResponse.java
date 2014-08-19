package com.pepsi.rest.activity.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="error")
@JsonInclude(value=Include.NON_NULL)
public class OAuthErrorResponse extends ErrorResponse {

    //Error response parameters
    public static final String OAUTH_ERROR_CODE = "error_code";
    public static final String OAUTH_ERROR_DESC = "error_desc";

    @JsonProperty(OAUTH_ERROR_CODE)
    private String errorCode;
    @JsonProperty(OAUTH_ERROR_DESC)
    private String errorDescription;

    public OAuthErrorResponse() {};

    public OAuthErrorResponse(OAuthErrCode errorCode, String errorDescription) {
        this.errorCode = errorCode.toString();
        this.errorDescription = errorDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "[" + this.errorCode + "] " + this.errorDescription;
    }

    @Override
    public String getErrMsg() {
        return toString();
    }

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
        INVALID_GRANT_PASSWORD ("The authentication failed on user %s");

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
