package com.pepsi.rest.oauth.provider;

import javax.annotation.Nonnull;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.pepsi.rest.commons.UUIDGenerator;
import com.pepsi.rest.oauth.provider.model.GrantType;
import com.pepsi.rest.oauth.provider.model.OAuthRequestQueryParameters;
import com.pepsi.rest.oauth.provider.model.OAuthErrorResponse;
import com.pepsi.rest.oauth.provider.model.OAuthRequestValidator;
import com.pepsi.rest.oauth.provider.model.OAuthTokenActivityError;
import com.pepsi.rest.oauth.provider.model.OAuthTokenResponse;
import com.pepsi.rest.server.exception.OAuthBadRequestException;

@Path("/oauth2/v1/token")
public class OAuthTokenActivity {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response generateToken(@HeaderParam(HttpHeaders.AUTHORIZATION) String authentication, @Context UriInfo uriInfo) throws OAuthBadRequestException {

        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        String oauthGrantType = OAuthRequestValidator.validateRequiredParameter(queryParameters, OAuthRequestQueryParameters.OAUTH_GRANT_TYPE);
        if (GrantType.PASSWORD.toString().equals(oauthGrantType)) {

            OAuthRequestValidator.validateRequiredParameters(queryParameters, 
                    OAuthRequestQueryParameters.OAUTH_USER_NAME, OAuthRequestQueryParameters.OAUTH_PASSWORD);
            String userName = queryParameters.getFirst(OAuthRequestQueryParameters.OAUTH_USER_NAME);
            String password = queryParameters.getFirst(OAuthRequestQueryParameters.OAUTH_PASSWORD);

            if (!autheticateUserPass(userName, password)) {
                OAuthErrorResponse oauthErrorResponse= OAuthErrorResponse.errorCode(OAuthTokenActivityError.INVALID_GRANT)
                        .errorDescription("The userName and/or password does not match our records").build();
                return Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=OAuth2")
                        .entity(oauthErrorResponse).build();
            }

        } else {
            // TODO: support other OAuth Grant Types
            throw new OAuthBadRequestException(OAuthErrorResponse.errorCode(OAuthTokenActivityError.UNSUPPORTED_GRANT_TYPE) 
                    .errorDescription("The authorization grant type is not supported by the authorization server").build());
        }

        // TODO: persist access token and refresh token to the database
        String accessToken = UUIDGenerator.randomUUID().toString();

        OAuthTokenResponse oauthTokenResponse = OAuthTokenResponse.accessToken(accessToken).expireInSeconds(3600).build();
        return Response.status(Status.OK).entity(oauthTokenResponse).build();
    }

    private boolean autheticateUserPass(@Nonnull String userName, @Nonnull String password) {
        // TODO: authenticate userName and password
        return true;
    }
    
   
    
}
