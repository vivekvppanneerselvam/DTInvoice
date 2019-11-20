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
