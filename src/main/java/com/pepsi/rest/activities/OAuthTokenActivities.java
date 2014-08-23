package com.pepsi.rest.activities;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.pepsi.rest.activity.model.OAuthErrors.OAuthErrCode;
import com.pepsi.rest.activity.model.OAuthErrors.OAuthErrDescFormatter;
import com.pepsi.rest.activity.model.OAuthRevokeTokenRequest;
import com.pepsi.rest.activity.model.OAuthTokenRequest;
import com.pepsi.rest.activity.model.OAuthTokenResponse;
import com.pepsi.rest.activity.model.OAuthTokenRequest.GrantType;
import com.pepsi.rest.repository.AuthenticationTokenRepository;
import com.pepsi.rest.repository.UserAuthorizationRepository;
import com.pepsi.rest.repository.exception.DuplicateKeyException;
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

    private static final String GENERATE_TOKEN_ERROR_MESSAGE = "Failed while attempting to fulfill generating token request due to %s: ";
    private static final String REVOKE_TOKEN_ERROR_MESSAGE = "Failed while attempting to fulfill revoking token request due to %s: ";

    @Inject
    private AuthenticationTokenRepository tokenRepository;
    @Inject
    private UserAuthorizationRepository userRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(@Context UriInfo uriInfo, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) 
            throws BadRequestException, InternalServerErrorException {
        try {
            OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.validateRequestFromMultiValuedParameters(uriInfo.getQueryParameters());
            return generateToken(oAuthTokenRequest, authorizationHeader);
        } catch (BadRequestException badRequest) {
            LOG.info(String.format(GENERATE_TOKEN_ERROR_MESSAGE, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
            throw badRequest;

        } catch (Exception internalFailure) {
            LOG.error(String.format(GENERATE_TOKEN_ERROR_MESSAGE, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
            throw new InternalServerErrorException(internalFailure);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateToken(final MultivaluedMap<String, String> formParameters, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) 
            throws BadRequestException, InternalServerErrorException {
        try {
            OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.validateRequestFromMultiValuedParameters(formParameters);
            return generateToken(oAuthTokenRequest, authorizationHeader);
        } catch (BadRequestException badRequest) {
            LOG.info(String.format(GENERATE_TOKEN_ERROR_MESSAGE, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
            throw badRequest;

        } catch (Exception internalFailure) {
            LOG.error(String.format(GENERATE_TOKEN_ERROR_MESSAGE, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
            throw new InternalServerErrorException(internalFailure);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response revokeToken(@Context UriInfo uriInfo) 
            throws BadRequestException, InternalServerErrorException {
        try {
            OAuthRevokeTokenRequest oAuthRevokeTokenRequest = OAuthRevokeTokenRequest.validateRequestFromMultiValuedParameters(uriInfo.getQueryParameters());
            tokenRepository.revokeToken(oAuthRevokeTokenRequest.getTokenType(), oAuthRevokeTokenRequest.getToken());
            
            return Response.status(Status.OK).build();
        } catch (BadRequestException badRequest) {
            LOG.info(String.format(REVOKE_TOKEN_ERROR_MESSAGE, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
            throw badRequest;

        } catch (Exception internalFailure) {
            LOG.error(String.format(REVOKE_TOKEN_ERROR_MESSAGE, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
            throw new InternalServerErrorException(internalFailure);
        }
    }
    
    private Response generateToken(@Nonnull OAuthTokenRequest oAuthTokenRequest, @Nullable String authorizationHeader) 
            throws BadRequestException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryClientException, RepositoryServerException {
        AuthenticationToken accessToken = null;
        GrantType oauthGrantType = oAuthTokenRequest.getGrantType();        

        switch (oauthGrantType) {
        case PASSWORD:
            accessToken = generateTokenForPassword(oAuthTokenRequest);
            break;
        case CLIENT_CREDENTIAL:
//            accessToken = generateTokenForClientCredential(oAuthTokenRequest, authorizationHeader);
//            break;
        case AUTHORIZATION_CODE:
//            accessToken = generateTokenForAuthorizationCode(oAuthTokenRequest);
//            break;
        default:
            // TODO: support other Grant Types
            throw new OAuthBadRequestException(OAuthErrCode.UNSUPPORTED_GRANT_TYPE,  
                    String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), oauthGrantType));
        }
        
        // Here we try one more time to persist the token only if we get back DuplicateKeyException. 
        // If we still fail after that, throw exception and log a fatal.
        try {
            tokenRepository.persistToken(accessToken);
        } catch (DuplicateKeyException duplicateKeyOnce) {
            //TODO: monitor how often this happens
            LOG.warn( String.format("Failed to persist token %s due to duplicate token already exists", accessToken.getToken()));
            accessToken = AuthenticationToken.updateTokenValue(accessToken);
            try {
                tokenRepository.persistToken(accessToken);
            } catch (DuplicateKeyException duplicateKeyAgain) {
                LOG.error(String.format("Failed to persist token %s for the second time due to duplicate token already exists", accessToken.getToken()));
                throw duplicateKeyAgain;
            }
        }

        OAuthTokenResponse oauthTokenResponse = new OAuthTokenResponse(accessToken);
        return Response.status(Status.OK).entity(oauthTokenResponse).build();
    }

    private @Nonnull AuthenticationToken generateTokenForPassword(@Nonnull OAuthTokenRequest oAuthTokenRequest) 
            throws OAuthBadRequestException, UnsupportedEncodingException, NoSuchAlgorithmException, RepositoryClientException, RepositoryServerException {
        String userName = oAuthTokenRequest.getUserName();
        String password = oAuthTokenRequest.getPassword();

        if (!autheticateUserPass(userName, password)) {
            throw new OAuthBadRequestException(OAuthErrCode.INVALID_GRANT, 
                    String.format(OAuthErrDescFormatter.INVALID_GRANT_PASSWORD.toString(), userName));
        }

        return AuthenticationToken.generateToken().tokenType(AuthenticationTokenType.ACCESS_TOKEN)
                .issuedAt(new DateTime()).userId(userName).build();
    }

    private boolean autheticateUserPass(@Nonnull String userName, @Nonnull String userPassword) 
            throws RepositoryClientException, RepositoryServerException, UnsupportedEncodingException, NoSuchAlgorithmException {
        UserAuthorizationInfo userAuthorizationInfo = userRepository.getUserAuthorizationInfo(userName);
        if (userAuthorizationInfo != null) {
            return UserPassAuthenticationHelper.authenticatePassword(userPassword, userAuthorizationInfo.getHashedPassword(), userAuthorizationInfo.getSalt());
        } 
        return false;
    }

    private @Nonnull AuthenticationToken generateTokenForClientCredential(@Nonnull OAuthTokenRequest oAuthTokenRequest, @Nullable String authorizationHeader) 
            throws BadRequestException, RepositoryClientException, RepositoryServerException {
        String clientId = oAuthTokenRequest.getClientId();
        
        if (!authenticateClient(authorizationHeader)) {
            throw new OAuthBadRequestException(OAuthErrCode.UNAUTHENTICATED_CLIENT, 
                    String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), clientId));
        } 
        // TODO: authorize client based on client information
        throw new OAuthBadRequestException(OAuthErrCode.UNAUTHORIZED_CLIENT, 
                String.format(OAuthErrDescFormatter.CLIENT_CREDENTIALS_NOT_PERMITTED.toString(), clientId));
    }

    private boolean authenticateClient(@Nullable String authorizationHeader) 
            throws BadRequestException, RepositoryClientException, RepositoryServerException {
        Authorizer basicAuthorizer = Authorizer.validateAuthorizationHeader(authorizationHeader, AuthenticationMethod.BASIC_AUTHENTICATION);
        // TODO: authenticate client based on clientId and client secret proof in the authorization header
        basicAuthorizer.authenticate(userRepository);
        return true;
    }

    private @Nonnull AuthenticationToken generateTokenForAuthorizationCode(@Nonnull OAuthTokenRequest oAuthTokenRequest, @Nullable String authorizationHeader) throws BadRequestException, RepositoryClientException, RepositoryServerException {
        
        String requestToken = oAuthTokenRequest.getRequestToken();
        String clientId = oAuthTokenRequest.getClientId();
        String redirectUri = oAuthTokenRequest.getRedirectUri();
        
        if (!authenticateClient(authorizationHeader)) {
            throw new OAuthBadRequestException(OAuthErrCode.UNAUTHENTICATED_CLIENT, 
                    String.format(OAuthErrDescFormatter.UNAUTHENTICATED_CLIENT.toString(), clientId));
        } 
        
        if (!exchangeRequestToken(requestToken, clientId, redirectUri)) {
            throw new OAuthBadRequestException(OAuthErrCode.INVALID_GRANT, 
                    String.format(OAuthErrDescFormatter.INVALID_GRANT_AUTHORIZATION_CODE.toString(), requestToken));
        }

        // TODO: support generating token for authorization code 
        throw new OAuthBadRequestException(OAuthErrCode.UNSUPPORTED_GRANT_TYPE,  
                String.format(OAuthErrDescFormatter.UNSUPPORTED_GRANT_TYPE.toString(), GrantType.AUTHORIZATION_CODE.toString()));
    }

    private boolean exchangeRequestToken(@Nonnull String requestToken, @Nonnull String clientId, @Nonnull String redirectURI) {
        // TODO: exchange request token for access token, the redirectURI and clientId should match the record in database
        return false;
    }
}
