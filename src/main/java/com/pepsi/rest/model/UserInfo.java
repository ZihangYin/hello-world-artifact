package com.pepsi.rest.model;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.pepsi.rest.model.UserAddress.UserAddressType;

@XmlRootElement
public class UserInfo {
    
    private String userID;
    private String userFirstName;
    private String userLastName;
    private long age; 
    private Map<UserAddressType, UserAddress> userAddresses;
    private Set<UserInfo> userContacts;
    private Set<CreditCardInfo> userCreditCardsInfo;
    
    public UserInfo(){}
    
    public UserInfo(String userID, String userFirstName, String userLastName, long age, Map<UserAddressType, UserAddress> userAddresses,
            Set<UserInfo> userContacts, Set<CreditCardInfo> userCreditCardsInfo) {        
        this.userID = userID;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.age = age;
        this.userAddresses = userAddresses;
        this.userContacts = userContacts;
        this.userCreditCardsInfo = userCreditCardsInfo;
    }
    
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public long getAge() {
        return age;
    }
    
    public void setAge(long age) {
        this.age = age;
    }

    public Map<UserAddressType, UserAddress> getUserAddresses() {
        return userAddresses;
    }

    public void setUserAddresses(Map<UserAddressType, UserAddress> userAddresses) {
        this.userAddresses = userAddresses;
    }

    public Set<UserInfo> getUserContacts() {
        return userContacts;
    }

    public void setUserContacts(Set<UserInfo> userContacts) {
        this.userContacts = userContacts;
    }

    public Set<CreditCardInfo> getUserCreditCardsInfo() {
        return userCreditCardsInfo;
    }

    public void setUserCreditCardsInfo(Set<CreditCardInfo> userCreditCardsInfo) {
        this.userCreditCardsInfo = userCreditCardsInfo;
    }
}
