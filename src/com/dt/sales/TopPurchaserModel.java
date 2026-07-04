package com.dt.sales;

public class TopPurchaserModel {

    private int rank;

    private int purchaserId;

    private String purchaserName;

    private int billCount;

    private double purchaseAmount;

    private double paidAmount;

    private double outstandingAmount;

    private double contributionPercentage;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TopPurchaserModel(
            int rank,
            int purchaserId,
            String purchaserName,
            int billCount,
            double purchaseAmount,
            double paidAmount,
            double outstandingAmount,
            double contributionPercentage) {

        this.rank = rank;
        this.purchaserId = purchaserId;
        this.purchaserName = purchaserName;
        this.billCount = billCount;
        this.purchaseAmount = purchaseAmount;
        this.paidAmount = paidAmount;
        this.outstandingAmount = outstandingAmount;
        this.contributionPercentage = contributionPercentage;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getRank() {
        return rank;
    }

    public int getPurchaserId() {
        return purchaserId;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public int getBillCount() {
        return billCount;
    }

    public double getPurchaseAmount() {
        return purchaseAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public double getContributionPercentage() {
        return contributionPercentage;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setPurchaserId(int purchaserId) {
        this.purchaserId = purchaserId;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }

    public void setBillCount(int billCount) {
        this.billCount = billCount;
    }

    public void setPurchaseAmount(double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public void setContributionPercentage(double contributionPercentage) {
        this.contributionPercentage = contributionPercentage;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    public boolean hasOutstanding() {

        return outstandingAmount > 0;
    }

    public String getStatus() {

        return outstandingAmount > 0
                ? "Pending"
                : "Settled";
    }

    public double getSettlementPercentage() {

        if (purchaseAmount == 0) {
            return 0;
        }

        return (paidAmount / purchaseAmount) * 100;
    }

    @Override
    public String toString() {

        return purchaserName +
                " - ₹" +
                String.format("%.2f", purchaseAmount);
    }
}