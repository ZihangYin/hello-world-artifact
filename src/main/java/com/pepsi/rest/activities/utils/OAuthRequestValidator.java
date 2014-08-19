package com.pepsi.rest.activities.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;

import com.pepsi.rest.activities.exception.OAuthBadRequestException;
import com.pepsi.rest.activity.model.OAuthErrorResponse;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrDescFormatter;

public class OAuthRequestValidator {

    /**
     * The required parameters are not provided in the request
     */
    public static void validateRequiredParameters(@Nonnull final MultivaluedMap<String, String> multiValuedParameters, @Nonnull String... requiredParas) 
            throws OAuthBadRequestException {

        StringBuilder errDescBuilder = null;
        for (String requiredPara : requiredParas) {
            String requiredParaVal = multiValuedParameters.getFirst(requiredPara);
            if (StringUtils.isBlank(requiredParaVal)) {
                if (errDescBuilder != null) {
                    errDescBuilder.append(", ");
                } else {
                    errDescBuilder = new StringBuilder();
                }
                errDescBuilder.append(requiredPara);
            }
        }

        if (errDescBuilder != null) {
            String errDesc = String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), errDescBuilder.toString());
            throw new OAuthBadRequestException(new OAuthErrorResponse(OAuthErrCode.MISSING_PARAMETERS, errDesc));
        }
    }

    public static String validateRequiredParameter(@Nonnull String requiredPara, @Nullable String requiredParaVal) 
            throws OAuthBadRequestException {

        if (StringUtils.isBlank(requiredParaVal)) {
            String errDesc = String.format(OAuthErrDescFormatter.MISSING_PARAMETERS.toString(), requiredPara);
            throw new OAuthBadRequestException(new OAuthErrorResponse(OAuthErrCode.MISSING_PARAMETERS, errDesc));
        } else {
            return requiredParaVal;
        }
    }
}
