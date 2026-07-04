package com.dt.purchaser;

public class PurchaserModel {

    private int id;

    private String purchaserCode;

    private String name;

    private String gstin;

    private String mobile;

    private String address;

    private double pending;

    private int bills;

    private int pendingBills;

    private int settledBills;

    private double totalPurchaseAmount;

    private double totalPaidAmount;

    private String lastPurchase;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PurchaserModel(
            int id,
            String purchaserCode,
            String name,
            String gstin,
            String mobile,
            String address,
            double pending,
            int bills,
            int pendingBills,
            int settledBills,
            double totalPurchaseAmount,
            double totalPaidAmount,
            String lastPurchase) {

        this.id = id;
        this.purchaserCode = purchaserCode;
        this.name = name;
        this.gstin = gstin;
        this.mobile = mobile;
        this.address = address;
        this.pending = pending;
        this.bills = bills;
        this.pendingBills = pendingBills;
        this.settledBills = settledBills;
        this.totalPurchaseAmount = totalPurchaseAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.lastPurchase = lastPurchase;
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
    // PURCHASER CODE
    // =====================================================

    public String getPurchaserCode() {
        return purchaserCode;
    }

    public void setPurchaserCode(String purchaserCode) {
        this.purchaserCode = purchaserCode;
    }

    // =====================================================
    // NAME
    // =====================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =====================================================
    // GSTIN
    // =====================================================

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    // =====================================================
    // MOBILE
    // =====================================================

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // =====================================================
    // ADDRESS
    // =====================================================

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // =====================================================
    // PENDING
    // =====================================================

    public double getPending() {
        return pending;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }

    // =====================================================
    // TOTAL BILLS
    // =====================================================

    public int getBills() {
        return bills;
    }

    public void setBills(int bills) {
        this.bills = bills;
    }

    // =====================================================
    // PENDING BILLS
    // =====================================================

    public int getPendingBills() {
        return pendingBills;
    }

    public void setPendingBills(int pendingBills) {
        this.pendingBills = pendingBills;
    }

    // =====================================================
    // SETTLED BILLS
    // =====================================================

    public int getSettledBills() {
        return settledBills;
    }

    public void setSettledBills(int settledBills) {
        this.settledBills = settledBills;
    }

    // =====================================================
    // TOTAL PURCHASE
    // =====================================================

    public double getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(double totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    // =====================================================
    // TOTAL PAID
    // =====================================================

    public double getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(double totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    // =====================================================
    // LAST PURCHASE
    // =====================================================

    public String getLastPurchase() {
        return lastPurchase;
    }

    public void setLastPurchase(String lastPurchase) {
        this.lastPurchase = lastPurchase;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    public boolean hasPendingAmount() {

        return pending > 0;
    }

    public boolean isFullySettled() {

        return pending <= 0;
    }

    @Override
    public String toString() {

        return purchaserCode + " - " + name;
    }
}