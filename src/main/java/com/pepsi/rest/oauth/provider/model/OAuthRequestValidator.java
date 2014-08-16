package com.pepsi.rest.oauth.provider.model;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedMap;

import com.pepsi.rest.server.exception.OAuthBadRequestException;

public class OAuthRequestValidator {

    /**
     * The required parameters are not provided in the request
     */
    public static final String MISSING_PARAMETERS = "missing_parameters";

    public static void validateRequiredParameters(@Nonnull final MultivaluedMap<String, String> queryParameters, @Nonnull String... requiredParas) 
            throws OAuthBadRequestException {

        StringBuilder errDescBuilder = null;
        for (String requiredPara : requiredParas) {
            String requiredParaVal = queryParameters.getFirst(requiredPara);
            if (requiredParaVal == null || requiredParaVal.isEmpty()) {
                if (errDescBuilder != null) {
                    errDescBuilder.append(", ");
                } else {
                    errDescBuilder = new StringBuilder();
                }
                errDescBuilder.append(requiredPara);
            }
        }

        if (errDescBuilder != null) {
            errDescBuilder.insert(0, "Missing values for required parameters: ");
            throw new OAuthBadRequestException(OAuthErrorResponse.errorCode(MISSING_PARAMETERS)
                    .errorDescription(errDescBuilder.toString()).build());
        }
    }

    public static String validateRequiredParameter(@Nonnull final MultivaluedMap<String, String> queryParameters, @Nonnull String requiredPara) 
            throws OAuthBadRequestException {

        String requiredParaVal = queryParameters.getFirst(requiredPara);
        if (requiredParaVal == null || requiredParaVal.isEmpty()) {
            String errDesc = "Missing values for required parameters: " + requiredPara;
            throw new OAuthBadRequestException(OAuthErrorResponse.errorCode(MISSING_PARAMETERS)
                    .errorDescription(errDesc).build());
        } else {
            return requiredParaVal;
        }
    }
}
