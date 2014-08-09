package com.pepsi.rest.activity;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jersey.repackaged.com.google.common.collect.Sets;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pepsi.rest.model.CreditCardInfo;
import com.pepsi.rest.model.UserAddress;
import com.pepsi.rest.model.UserInfo;
import com.pepsi.rest.model.UserAddress.UserAddressType;
import com.pepsi.rest.server.GrizzlyServerTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetUserInfoActivityTest extends GrizzlyServerTestBase {

    private static WebTarget webTarget;
    
    @BeforeClass
    public static void setUpClient() throws Exception {        
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new GrizzlyConnectorProvider());
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).build();        
        client.register(HttpAuthenticationFeature.basic("username", "password"));        
        webTarget = client.target(httpURI);
    }
    
    @Test
    public void testGetUserInfoInJsonHappyCase() {        
        Response response = webTarget.path("api/v1/users/test.json").request().accept(MediaType.APPLICATION_JSON).get();     

        assertEquals(200, response.getStatus());

        UserInfo responseUserInfo = response.readEntity(UserInfo.class);

        assertEquals("test", responseUserInfo.getUserID());
        assertEquals("firstName", responseUserInfo.getUserFirstName());
        assertEquals("lastName", responseUserInfo.getUserLastName());
        assertEquals(27, responseUserInfo.getAge());               

        Map<UserAddressType, UserAddress> responseUserAddresses = responseUserInfo.getUserAddresses(); 
        UserAddress expectedUserHomeAddress = new UserAddress("home-country", "home-state", "home-city",
                "home-street", "00000");
        UserAddress expectedUserWorkAddress = new UserAddress("work-country", "work-state", "work-city",
                "work-street", "10000");

        assertEquals(2, responseUserAddresses.size());
        assertEquals(expectedUserHomeAddress, responseUserAddresses.get(UserAddressType.HOME));
        assertEquals(expectedUserWorkAddress, responseUserAddresses.get(UserAddressType.WORK));

        // For elements not showing up in the JSON, it will be considered as empty.
        assertEquals(0, responseUserInfo.getUserContacts().size());

        CreditCardInfo expectedCreditCardInfo1 = new CreditCardInfo("firstName", "lastName", "cardNumber1",
                2014, 12, "securityCode1", expectedUserWorkAddress);
        CreditCardInfo expectedCreditCardInfo2 = new CreditCardInfo("firstName", "lastName", "cardNumber2",
                2015, 1, "securityCode2", expectedUserWorkAddress);
        assertEquals(2, responseUserInfo.getUserCreditCardsInfo().size());
        assertEquals(Sets.newHashSet(expectedCreditCardInfo1, expectedCreditCardInfo2), responseUserInfo.getUserCreditCardsInfo());
    }
    
    @Test
    public void testGetUserInfoInXMLHappyCase() { 
        Response response = webTarget.path("api/v1/users/test.xml").request().get();     

        assertEquals(200, response.getStatus());

        UserInfo responseUserInfo = response.readEntity(UserInfo.class);

        assertEquals("test", responseUserInfo.getUserID());
        assertEquals("firstName", responseUserInfo.getUserFirstName());
        assertEquals("lastName", responseUserInfo.getUserLastName());
        assertEquals(27, responseUserInfo.getAge());               

        Map<UserAddressType, UserAddress> responseUserAddresses = responseUserInfo.getUserAddresses(); 
        UserAddress expectedUserHomeAddress = new UserAddress("home-country", "home-state", "home-city",
                "home-street", "00000");
        UserAddress expectedUserWorkAddress = new UserAddress("work-country", "work-state", "work-city",
                "work-street", "10000");

        assertEquals(2, responseUserAddresses.size());
        assertEquals(expectedUserHomeAddress, responseUserAddresses.get(UserAddressType.HOME));
        assertEquals(expectedUserWorkAddress, responseUserAddresses.get(UserAddressType.WORK));

        // For elements not showing up in the XML, it will be considered as null.
        assertNull(responseUserInfo.getUserContacts());

        CreditCardInfo expectedCreditCardInfo1 = new CreditCardInfo("firstName", "lastName", "cardNumber1",
                2014, 12, "securityCode1", expectedUserWorkAddress);
        CreditCardInfo expectedCreditCardInfo2 = new CreditCardInfo("firstName", "lastName", "cardNumber2",
                2015, 1, "securityCode2", expectedUserWorkAddress);
        assertEquals(2, responseUserInfo.getUserCreditCardsInfo().size());
        assertEquals(Sets.newHashSet(expectedCreditCardInfo1, expectedCreditCardInfo2), responseUserInfo.getUserCreditCardsInfo());
    }

    @Test
    public void testGetUserInfoInXMLWithMismatchAcceptedMediaType() { 
        Response response = webTarget.path("api/v1/users/test.xml").request().accept(MediaType.APPLICATION_JSON).get();     

        assertEquals(406, response.getStatus());
    }
}
