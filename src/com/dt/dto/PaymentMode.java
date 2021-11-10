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
public enum PaymentMode {
    CASH("cash"),
    CHEQUE("cheque"),
    DD("dd"),
    BANKTRANSFER("banktransfer");
    
    private final String value;
    
    private PaymentMode(String val) {
        value = val;
    }
    
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
       switch(this) {
           case CASH:
               return "Cash";
           case CHEQUE:
               return "Cheque";
           case DD:
               return "DD";
           case BANKTRANSFER:
               return "Bank Transfer";
           default:
               return "Unknown Value";
       }
    }
    
}
