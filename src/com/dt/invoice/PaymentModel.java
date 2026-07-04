package com.dt.invoice;

public class PaymentModel {

    private int id;

    private int invoiceId;

    private int customerId;

    private String paymentMode;

    private double paidAmount;

    private String paymentDate;

    private String remarks;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public PaymentModel() {
    }

    // =====================================================
    // PARAMETERIZED CONSTRUCTOR
    // =====================================================

    public PaymentModel(int id,
                        int invoiceId,
                        int customerId,
                        String paymentMode,
                        double paidAmount,
                        String paymentDate,
                        String remarks) {

        this.id = id;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.paymentMode = paymentMode;
        this.paidAmount = paidAmount;
        this.paymentDate = paymentDate;
        this.remarks = remarks;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getRemarks() {
        return remarks;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}