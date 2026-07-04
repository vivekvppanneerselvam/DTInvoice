package com.dt.purchaser;

public class BillModel {

    private int id;

    private String billNo;

    private int purchaserId;

    private String purchaserName;

    private String billDate;

    private double totalAmount;

    private double paidAmount;

    private double balanceAmount;

    private String paymentStatus;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BillModel(
            int id,
            String billNo,
            int purchaserId,
            String purchaserName,
            String billDate,
            double totalAmount,
            double paidAmount,
            double balanceAmount,
            String paymentStatus) {

        this.id = id;
        this.billNo = billNo;
        this.purchaserId = purchaserId;
        this.purchaserName = purchaserName;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.balanceAmount = balanceAmount;
        this.paymentStatus = paymentStatus;
    }

    // =====================================================
    // ID
    // =====================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // =====================================================
    // BILL NO
    // =====================================================

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    // =====================================================
    // PURCHASER ID
    // =====================================================

    public int getPurchaserId() {
        return purchaserId;
    }

    public void setPurchaserId(int purchaserId) {
        this.purchaserId = purchaserId;
    }

    // =====================================================
    // PURCHASER NAME
    // =====================================================

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    // =====================================================
    // BILL DATE
    // =====================================================

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    // =====================================================
    // TOTAL AMOUNT
    // =====================================================

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // =====================================================
    // PAID AMOUNT
    // =====================================================

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    // =====================================================
    // BALANCE AMOUNT
    // =====================================================

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    // =====================================================
    // PAYMENT STATUS
    // =====================================================

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    public boolean isPaid() {

        return balanceAmount <= 0;
    }

    public boolean isPending() {

        return balanceAmount > 0;
    }

    public double getCollectionPercentage() {

        if (totalAmount <= 0)
            return 0;

        return (paidAmount / totalAmount) * 100;
    }

    @Override
    public String toString() {

        return billNo + " - " + purchaserName;
    }
}