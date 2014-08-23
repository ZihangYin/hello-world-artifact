package com.pepsi.rest.activity.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="error")
@JsonInclude(value=Include.NON_NULL)
public class ErrorResponse {
    
    private static final String ERROR_TYPE = "error_type";
    private static final String ERROR_CODE = "error_code";
    private static final String ERROR_DESC = "error_desc";
    
    @JsonProperty(ERROR_TYPE)
    private String errorType;
    @JsonProperty(ERROR_CODE)
    private String errorCode;
    @JsonProperty(ERROR_DESC)
    private String errorDescription;
    
    public ErrorResponse(){}

    public ErrorResponse(String errorType, String errorCode, String errorDescription) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
    
    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
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
        return "ErrorResponse [errorType=" + errorType + ", errorCode="
                + errorCode + ", errorDescription=" + errorDescription + "]";
    }
    
}
