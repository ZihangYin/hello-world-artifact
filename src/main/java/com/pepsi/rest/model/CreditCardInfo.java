package com.pepsi.rest.model;

public class CreditCardInfo {
    
    private String firstName;
    private String lastName;
    private String cardNumber;
    private int expirationYear;
    private int expirationMonth;
    private String securityCode;
    private UserAddress billingAddress;
    
    public CreditCardInfo(){}
    
    public CreditCardInfo(String firstName, String lastName, String cardNumber,
            int expirationYear, int expirationMonth, String securityCode, UserAddress billingAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cardNumber = cardNumber;
        this.expirationYear = expirationYear;
        this.expirationMonth = expirationMonth;
        this.securityCode = securityCode;
        this.billingAddress = billingAddress;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public int getExpirationYear() {
        return expirationYear;
    }
    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }
    public int getExpirationMonth() {
        return expirationMonth;
    }
    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }
    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
    public UserAddress getBillingAddress() {
        return billingAddress;
    }
    public void setBillingAddress(UserAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public String toString() {
        return "CreditCardInfo [firstName=" + firstName + ", lastName="
                + lastName + ", cardNumber=" + cardNumber + ", expirationYear="
                + expirationYear + ", expirationMonth=" + expirationMonth
                + ", securityCode=" + securityCode + ", billingAddress="
                + billingAddress + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((billingAddress == null) ? 0 : billingAddress.hashCode());
        result = prime * result
                + ((cardNumber == null) ? 0 : cardNumber.hashCode());
        result = prime * result + expirationMonth;
        result = prime * result + expirationYear;
        result = prime * result
                + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result
                + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result
                + ((securityCode == null) ? 0 : securityCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CreditCardInfo other = (CreditCardInfo) obj;
        if (billingAddress == null) {
            if (other.billingAddress != null)
                return false;
        } else if (!billingAddress.equals(other.billingAddress))
            return false;
        if (cardNumber == null) {
            if (other.cardNumber != null)
                return false;
        } else if (!cardNumber.equals(other.cardNumber))
            return false;
        if (expirationMonth != other.expirationMonth)
            return false;
        if (expirationYear != other.expirationYear)
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (securityCode == null) {
            if (other.securityCode != null)
                return false;
        } else if (!securityCode.equals(other.securityCode))
            return false;
        return true;
    }    
}
