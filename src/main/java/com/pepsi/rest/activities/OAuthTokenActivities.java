package com.pepsi.rest.activities;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.pepsi.rest.activities.exception.BadRequestException;
import com.pepsi.rest.activities.exception.InternalServerErrorException;
import com.pepsi.rest.activities.exception.OAuthBadRequestException;
import com.pepsi.rest.activities.utils.OAuthRequestValidator;
import com.pepsi.rest.activity.model.OAuthErrorResponse;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrorResponse.OAuthErrDescFormatter;
import com.pepsi.rest.activity.model.OAuthTokenRequest;
import com.pepsi.rest.activity.model.OAuthTokenResponse;
import com.pepsi.rest.activity.model.OAuthTokenRequest.GrantType;
import com.pepsi.rest.commons.UUIDGenerator;
import com.pepsi.rest.repository.AuthenticationTokenRepository;
import com.pepsi.rest.repository.UserAuthorizationRepository;
import com.pepsi.rest.repository.exception.RepositoryClientException;
import com.pepsi.rest.repository.exception.RepositoryServerException;
import com.pepsi.rest.repository.model.AuthenticationToken;
import com.pepsi.rest.repository.model.UserAuthorizationInfo;
import com.pepsi.rest.repository.model.AuthenticationToken.AuthenticationTokenType;
import com.pepsi.rest.server.filter.utils.Authorizer;
import com.pepsi.rest.server.filter.utils.Authorizer.AuthenticationMethod;
import com.pepsi.rest.utils.UserPassAuthenticationHelper;

@Path("/oauth2/v1/token")
public class OAuthTokenActivities {
    private static final Logger LOG = LogManager.getLogger(OAuthTokenActivities.class);
    private static final String generateTokenErrMsg = "Failed while attempting to fulfill generating token request due to %s: ";

    @Inject
    private AuthenticationTokenRepository tokenRepository;
    @Inject
    private UserAuthorizationRepository userRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(@Context UriInfo uriInfo, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) 
            throws BadRequestException, InternalServerErrorException {
        try {
            OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.fromMultiValuedParameters(uriInfo.getQueryParameters());
            return generateToken(oAuthTokenRequest, authorizationHeader);
        } catch (BadRequestException badRequest) {
            LOG.info(String.format(generateTokenErrMsg, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
            throw badRequest;

        } catch (Exception internalFailure) {
            LOG.error(String.format(generateTokenErrMsg, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
            throw new InternalServerErrorException();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(final MultivaluedMap<String, String> formParameters, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) 
            throws BadRequestException, InternalServerErrorException {
        try {
            OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.fromMultiValuedParameters(formParameters);
            return generateToken(oAuthTokenRequest, authorizationHeader);
        } catch (BadRequestException badRequest) {
            LOG.info(String.format(generateTokenErrMsg, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
            throw badRequest;

        } catch (Exception internalFailure) {
            LOG.error(String.format(generateTokenErrMsg, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
            throw new InternalServerErrorException();
        }
    }

    private Response generateToken(@Nonnull OAuthTokenRequest oAuthTokenRequest, @Nullable String authorizationHeader) 
            throws BadRequestException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryServerException {
        AuthenticationToken accessToken = null;
        String oauthGrantType = oAuthTokenRequest.getGrantType();        

        if (GrantType.PASSWORD.toString().equals(oauthGrantType)) {
            accessToken = generateTokenForPassword(oAuthTokenRequest);
        } else if (GrantType.CLIENT_CREDENTIAL.toString().equals(oauthGrantType)) {
            accessToken = generateTokenForClientCredential(oAuthTokenRequest, authorizationHeader);
        } else if (GrantType.AUTHORIZATION_CODE.toString().equals(oauthGrantType)) {
            accessToken = generateTokenForAuthorizationCode(oAuthTokenRequest);
        } else {
            // TODO: support REFRESH_TOKEN Grant Types
            throw new OAuthBadRequestException(new OAuthErrorResponse(OAuthErrCode.UNSUPPORTED_GRANT_TYPE,  
                    String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), oauthGrantType)));
        }

        OAuthTokenResponse oauthTokenResponse = new OAuthTokenResponse(accessToken);
        try {
            tokenRepository.persistToken(accessToken);
        } catch (RepositoryClientException rce) {

        }
        return Response.status(Status.OK).entity(oauthTokenResponse).build();
    }

    private @Nonnull AuthenticationToken generateTokenForPassword(@Nonnull OAuthTokenRequest oAuthTokenRequest) 
            throws OAuthBadRequestException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryServerException {
        String userName = OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_USER_NAME, oAuthTokenRequest.getUserName());
        String password = OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_PASSWORD, oAuthTokenRequest.getPassword());

        if (!autheticateUserPass(userName, password)) {
            throw new OAuthBadRequestException(new OAuthErrorResponse (OAuthErrCode.INVALID_GRANT, 
                    String.format(OAuthErrDescFormatter.INVALID_GRANT_PASSWORD.toString(), userName)));
        }

        return AuthenticationToken.buildToken().token(UUIDGenerator.randomUUID().toString())
                .tokenType(AuthenticationTokenType.ACCESS_TOKEN).issuedAt(new DateTime()).build();
    }

    private @Nonnull boolean autheticateUserPass(@Nonnull String userName, @Nonnull String userPassword) 
            throws RepositoryServerException, UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            UserAuthorizationInfo userAuthorizationInfo = userRepository.getUserAuthorizationInfo(userName);
            if (userAuthorizationInfo != null) {
                UserPassAuthenticationHelper.authenticatePassword(userPassword, userAuthorizationInfo.getHashedPassword(), userAuthorizationInfo.getSalt());
            }
        } catch (RepositoryClientException rce) {

        }
        return true;
    }

    private AuthenticationToken generateTokenForClientCredential(@Nonnull OAuthTokenRequest oAuthTokenRequest, @Nullable String authorizationHeader) 
            throws BadRequestException {
        String clientId = OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_CLIENT_ID, oAuthTokenRequest.getClientId());
        if (!authenticateClient(authorizationHeader)) {
            throw new OAuthBadRequestException(new OAuthErrorResponse (OAuthErrCode.UNAUTHENTICATED_CLIENT, 
                    String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), clientId)));
        } 

        // TODO: authorize client based on client information
        throw new OAuthBadRequestException(new OAuthErrorResponse (OAuthErrCode.UNAUTHORIZED_CLIENT, 
                String.format(OAuthErrDescFormatter.CLIENT_CREDENTIALS_NOT_PERMITTED.toString(), clientId)));
    }

    private boolean authenticateClient(@Nullable String authorizationHeader) 
            throws BadRequestException {
        Authorizer.validateAuthorizationHeader(authorizationHeader, AuthenticationMethod.BASIC_AUTHENTICATION);
        // TODO: authenticate client based on clientId and client secret proof in the authorization header
        return false;
    }

    private AuthenticationToken generateTokenForAuthorizationCode(@Nonnull OAuthTokenRequest oAuthTokenRequest) throws OAuthBadRequestException {
        String requestToken = OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_REQUEST_TOKEN, oAuthTokenRequest.getRequestToken());
        String redirectURI = OAuthRequestValidator.validateRequiredParameter(OAuthTokenRequest.OAUTH_REDIRECT_URI, oAuthTokenRequest.getRedirectUri());

        if (!exchangeRequestToken(requestToken, redirectURI)) {
            throw new OAuthBadRequestException(new OAuthErrorResponse (OAuthErrCode.INVALID_GRANT, 
                    String.format(OAuthErrDescFormatter.INVALID_GRANT_AUTHORIZATION_CODE.toString(), requestToken)));
        }

        // TODO: support generating token for authorization code 
        throw new OAuthBadRequestException(new OAuthErrorResponse(OAuthErrCode.UNSUPPORTED_GRANT_TYPE,  
                String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), GrantType.AUTHORIZATION_CODE.toString())));
    }

    private boolean exchangeRequestToken(@Nonnull String requestToken, @Nonnull String redirectURI) {
        // TODO: exchange request token for access token
        return false;
    }
}
