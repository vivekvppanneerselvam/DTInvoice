package com.dt.customer;

public class CustomerModel {

    private int id;

    private String customerType;

    private String customerCode;

    private String customerName;

    private String gstNumber;

    private String phoneNo;

    private String altPhoneNo;

    private String email;

    private String address1;

    private String address2;

    private String city;

    private String state;

    private String pincode;

    private double openingBalance;

    private String balanceType;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public CustomerModel() {
    }

    public CustomerModel(
            int id,
            String customerType,
            String customerCode,
            String customerName,
            String gstNumber,
            String phoneNo,
            String altPhoneNo,
            String email,
            String address1,
            String address2,
            String city,
            String state,
            String pincode,
            double openingBalance,
            String balanceType) {

        this.id = id;

        this.customerType = customerType;

        this.customerCode = customerCode;

        this.customerName = customerName;

        this.gstNumber = gstNumber;

        this.phoneNo = phoneNo;

        this.altPhoneNo = altPhoneNo;

        this.email = email;

        this.address1 = address1;

        this.address2 = address2;

        this.city = city;

        this.state = state;

        this.pincode = pincode;

        this.openingBalance = openingBalance;

        this.balanceType = balanceType;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public String getCustomerType() {
        return customerType;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAltPhoneNo() {
        return altPhoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public String getBalanceType() {
        return balanceType;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setAltPhoneNo(String altPhoneNo) {
        this.altPhoneNo = altPhoneNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    // =====================================================
    // DISPLAY
    // =====================================================

    @Override
    public String toString() {

        return customerName
                +
                " - "
                +
                phoneNo;
    }
}