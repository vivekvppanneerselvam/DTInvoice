package com.dt.sales;

public class DashboardSummaryModel {

    // =====================================================
    // SALES
    // =====================================================

    private double totalSales;

    private double totalPurchase;

    private double grossProfit;

    private double netProfit;

    private double totalExpense;

    // =====================================================
    // GST
    // =====================================================

    private double totalGST;

    private int gstBills;

    private int nonGstBills;

    // =====================================================
    // BILLS
    // =====================================================

    private int totalInvoices;

    private double averageBillValue;

    // =====================================================
    // CREDIT
    // =====================================================

    private double creditSales;

    private double collectedAmount;

    private double outstandingAmount;

    private double recoveryPercentage;

    // =====================================================
    // TOP PERFORMERS
    // =====================================================

    private String topCustomer;

    private double topCustomerSales;

    private String topItem;

    private double topItemSales;

    // =====================================================
    // BUSINESS HEALTH
    // =====================================================

    private double healthScore;

    private String healthStatus;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public DashboardSummaryModel() {
    }

    // =====================================================
    // SALES
    // =====================================================

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(double totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public double getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(double grossProfit) {
        this.grossProfit = grossProfit;
    }

    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    // =====================================================
    // GST
    // =====================================================

    public double getTotalGST() {
        return totalGST;
    }

    public void setTotalGST(double totalGST) {
        this.totalGST = totalGST;
    }

    public int getGstBills() {
        return gstBills;
    }

    public void setGstBills(int gstBills) {
        this.gstBills = gstBills;
    }

    public int getNonGstBills() {
        return nonGstBills;
    }

    public void setNonGstBills(int nonGstBills) {
        this.nonGstBills = nonGstBills;
    }

    // =====================================================
    // INVOICES
    // =====================================================

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public double getAverageBillValue() {
        return averageBillValue;
    }

    public void setAverageBillValue(double averageBillValue) {
        this.averageBillValue = averageBillValue;
    }

    // =====================================================
    // CREDIT
    // =====================================================

    public double getCreditSales() {
        return creditSales;
    }

    public void setCreditSales(double creditSales) {
        this.creditSales = creditSales;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(double collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public double getRecoveryPercentage() {
        return recoveryPercentage;
    }

    public void setRecoveryPercentage(double recoveryPercentage) {
        this.recoveryPercentage = recoveryPercentage;
    }

    // =====================================================
    // TOP CUSTOMER
    // =====================================================

    public String getTopCustomer() {
        return topCustomer;
    }

    public void setTopCustomer(String topCustomer) {
        this.topCustomer = topCustomer;
    }

    public double getTopCustomerSales() {
        return topCustomerSales;
    }

    public void setTopCustomerSales(double topCustomerSales) {
        this.topCustomerSales = topCustomerSales;
    }

    // =====================================================
    // TOP ITEM
    // =====================================================

    public String getTopItem() {
        return topItem;
    }

    public void setTopItem(String topItem) {
        this.topItem = topItem;
    }

    public double getTopItemSales() {
        return topItemSales;
    }

    public void setTopItemSales(double topItemSales) {
        this.topItemSales = topItemSales;
    }

    // =====================================================
    // HEALTH SCORE
    // =====================================================

    public double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(double healthScore) {
        this.healthScore = healthScore;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    public String getFormattedHealthScore() {

        return String.format(
                "%.0f / 100",
                healthScore
        );
    }

    public boolean isExcellent() {

        return healthScore >= 85;
    }

    public boolean isGood() {

        return healthScore >= 70;
    }

    public boolean isAverage() {

        return healthScore >= 50;
    }

    @Override
    public String toString() {

        return "DashboardSummaryModel{" +
                "totalSales=" + totalSales +
                ", grossProfit=" + grossProfit +
                ", netProfit=" + netProfit +
                ", totalInvoices=" + totalInvoices +
                '}';
    }
}