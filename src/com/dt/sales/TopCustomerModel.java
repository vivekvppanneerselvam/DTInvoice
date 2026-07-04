package com.dt.sales;

public class TopCustomerModel {

    private int rank;

    private int customerId;

    private String customerName;

    private int totalBills;

    private double totalSales;

    private double totalProfit;

    private double outstandingAmount;

    private double contributionPercentage;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public TopCustomerModel(
            int rank,
            int customerId,
            String customerName,
            int totalBills,
            double totalSales,
            double totalProfit,
            double outstandingAmount,
            double contributionPercentage) {

        this.rank = rank;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalBills = totalBills;
        this.totalSales = totalSales;
        this.totalProfit = totalProfit;
        this.outstandingAmount = outstandingAmount;
        this.contributionPercentage = contributionPercentage;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getRank() {
        return rank;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public double getTotalProfit() {
        return totalProfit;
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

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setTotalBills(int totalBills) {
        this.totalBills = totalBills;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
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

    public String getFormattedContribution() {

        return String.format(
                "%.2f%%",
                contributionPercentage
        );
    }

    public String getCustomerGrade() {

        if (totalSales >= 100000) {
            return "A";
        }

        if (totalSales >= 50000) {
            return "B";
        }

        if (totalSales >= 25000) {
            return "C";
        }

        return "D";
    }

    @Override
    public String toString() {

        return "TopCustomerModel{" +
                "customerName='" + customerName + '\'' +
                ", totalSales=" + totalSales +
                ", totalBills=" + totalBills +
                '}';
    }
}
