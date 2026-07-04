/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dt.dto;


/**
 * @author vivek
 *
 */
public class FirmDetails {
    /*
    create table firm_details(
firm_name varchar(70) not null,
address varchar(120) not null,
phone_numbers varchar(120),
email_address varchar(50),
logo blob(512K))
    
    */
    
    private String firmName;
    private String address;
    private String phoneNumbers;
    private String emailAddress;
    private byte[] logo;
    private String firmSubName;
    private String gstNumber;
    private String fssaiNumber;
    private String reviewUrl;

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }
    public String getFirmSubName() {
        return firmSubName;
    }

    public void setFirmSubName(String firmSubName) {
        this.firmSubName = firmSubName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getFssaiNumber() {
        return fssaiNumber;
    }

    public void setFssaiNumber(String fssaiNumber) {
        this.fssaiNumber = fssaiNumber;
    }
    
    public String getFirmName() {
        return firmName;
    }
    
    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhoneNumbers() {
        return phoneNumbers;
    }
    
    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public byte[] getLogo() {
        return logo;
    }
    
    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
    
}
