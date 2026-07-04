package com.dt.invoice;

import java.time.LocalDateTime;

public class InvoiceModel {

    private int id;

    private String invoiceNo;

    private int customerId;

    private String saleType;

    private String paymentMode;

    private LocalDateTime billDate;

    private double subTotal;

    private double discount;

    private double deliveryCharge;

    private double gstTotal;

    private double grandTotal;

    private double paidAmount;

    private double balanceAmount;

    private String paymentStatus;

    private String notes;
    private String customerName;

    private String customerPhone;

    private String customerGST;

    private String customerAddress;

    public InvoiceModel() {
    }

    public void setCustomerAddress(String customerAddress) {

        this.customerAddress = customerAddress;
    }
    public String getCustomerAddress() {

        return customerAddress;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerGST(String customerGST) {
        this.customerGST = customerGST;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerGST() {
        return customerGST;
    }

    public int getId() {
        return id;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getSaleType() {
        return saleType;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public double getGstTotal() {
        return gstTotal;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public void setGstTotal(double gstTotal) {
        this.gstTotal = gstTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setGSTTotal(double gstTotal) {
        this.gstTotal = gstTotal;
    }

    public double getGSTTotal() {
        return gstTotal;
    }
}