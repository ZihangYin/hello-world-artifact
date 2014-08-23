//package com.pepsi.rest.activities;
//
//import javax.inject.Inject;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.core.Response;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.pepsi.rest.activities.exception.BadRequestException;
//import com.pepsi.rest.activities.exception.InternalServerErrorException;
//import com.pepsi.rest.activity.model.OAuthTokenRequest;
//import com.pepsi.rest.repository.UserAuthorizationRepository;

//@Path("/v1/new_users")
//public class UsersActivity {
//    private static final Logger LOG = LogManager.getLogger(UsersActivity.class);
//    
//    @Inject
//    private UserAuthorizationRepository userRepository;
//    
//    @POST
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response createNewUser(final MultivaluedMap<String, String> formParameters) 
//            throws BadRequestException, InternalServerErrorException {
//        try {
//            OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.validateRequestFromMultiValuedParameters(formParameters);
//            return generateToken(oAuthTokenRequest, authorizationHeader);
//        } catch (BadRequestException badRequest) {
//            LOG.info(String.format(ERROR_MESSAGE, BadRequestException.BAD_REQUEST) + badRequest.getMessage());
//            throw badRequest;
//
//        } catch (Exception internalFailure) {
//            LOG.error(String.format(ERROR_MESSAGE, InternalServerErrorException.INTERNAL_FAILURE), internalFailure);
//            throw new InternalServerErrorException();
//        }
//    }
//}
