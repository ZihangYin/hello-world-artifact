package com.pepsi.rest.oauth.provider.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name="error")
public class OAuthErrorResponse {
    
    //Error response parameters
    public static final String OAUTH_ERROR_CODE = "error_code";
    public static final String OAUTH_ERROR_DESC = "error_desc";

    @JsonProperty(OAUTH_ERROR_CODE)
    private String errorCode;
    @JsonProperty(OAUTH_ERROR_DESC)
    private String errorDescription;

    private OAuthErrorResponse() {};
    
    private OAuthErrorResponse(String errorCode, String errorDescription) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public static OAuthErrorResponseBuilder errorCode(String errorCode) {
        return new OAuthErrorResponseBuilder().errorCode(errorCode);
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
        return "OAuthErrorResponse [errorCode=" + errorCode + ", errorDescription=" + errorDescription + "]";
    }
    
    public static class OAuthErrorResponseBuilder {
        
        private String errorCode;
        private String errorDescription;
        
        public OAuthErrorResponseBuilder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public OAuthErrorResponseBuilder errorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
            return this;
        }
        
        public OAuthErrorResponse build() {
            return new OAuthErrorResponse(errorCode, errorDescription);
        }
    }
}
