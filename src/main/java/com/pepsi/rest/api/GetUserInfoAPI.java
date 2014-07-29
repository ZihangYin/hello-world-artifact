package com.pepsi.rest.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.pepsi.rest.model.CreditCardInfo;
import com.pepsi.rest.model.UserAddress;
import com.pepsi.rest.model.UserInfo;
import com.pepsi.rest.model.UserAddress.UserAddressType;

/**
 * Root resource (exposed at "api" path)
 */
@Path("api")
public class GetUserInfoAPI {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as either "application/json" or "application/xml" media type specified in the request header.
     * 
     * How to verify by using CURL command
     * curl -i http://localhost:8080/json/user/test -H "Accept: application/xml"
     * curl -i http://localhost:8080/json/user/test -H "Accept: application/json"
     *
     * @return UserInfo that will be returned as either "application/json" or "application/xml" media type specified in the request header.
     */
    @GET
    @Path("user/{userID}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public UserInfo getCustomer(@PathParam("userID") String userID) {
                
        UserAddress userHomeAddress = new UserAddress("home-country", "home-state", "home-city",
                "home-street", "00000");
        UserAddress userWorkAddress = new UserAddress("work-country", "work-state", "work-city",
                "work-street", "10000");
        Map<UserAddressType, UserAddress> userAddresses = new HashMap<>();
        userAddresses.put(UserAddressType.HOME, userHomeAddress);
        userAddresses.put(UserAddressType.WORK, userWorkAddress);        
        
        Set<UserInfo> userContacts = new HashSet<>();                
        
        CreditCardInfo creditCardInfo1 = new CreditCardInfo("firstName", "lastName", "cardNumber1",
                2014, 12, "securityCode1", userWorkAddress);
        CreditCardInfo creditCardInfo2 = new CreditCardInfo("firstName", "lastName", "cardNumber2",
                2015, 1, "securityCode2", userWorkAddress);
        Set<CreditCardInfo> userCreditCardsInfo = new HashSet<>();
        userCreditCardsInfo.add(creditCardInfo1);
        userCreditCardsInfo.add(creditCardInfo2);
        
        UserInfo userInfo = new UserInfo(userID, "firstName", "lastName", 27, userAddresses, userContacts, userCreditCardsInfo);
        
        return userInfo;
    }
}


