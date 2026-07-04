package com.dt.sales;

public class SalesAnalyticsModel {

    // =====================================================
    // INVOICE DETAILS
    // =====================================================

    private int id;

    private String invoiceNo;

    private String customerName;

    private String saleType;

    private String paymentMode;

    private String billDate;

    // =====================================================
    // SALES
    // =====================================================

    private double grandTotal;

    private double paidAmount;

    private double balanceAmount;

    private double gstTotal;

    // =====================================================
    // PURCHASE / PROFIT
    // =====================================================

    private double purchaseTotal;

    private double profit;

    private double profitPercentage;

    // =====================================================
    // FLAGS
    // =====================================================

    private boolean gstEnabled;

    private boolean creditSale;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public SalesAnalyticsModel(
            int id,
            String invoiceNo,
            String customerName,
            String saleType,
            String paymentMode,
            String billDate,
            double grandTotal,
            double paidAmount,
            double balanceAmount,
            double gstTotal,
            double purchaseTotal,
            double profit,
            double profitPercentage,
            boolean gstEnabled,
            boolean creditSale) {

        this.id = id;
        this.invoiceNo = invoiceNo;
        this.customerName = customerName;
        this.saleType = saleType;
        this.paymentMode = paymentMode;
        this.billDate = billDate;

        this.grandTotal = grandTotal;
        this.paidAmount = paidAmount;
        this.balanceAmount = balanceAmount;
        this.gstTotal = gstTotal;

        this.purchaseTotal = purchaseTotal;
        this.profit = profit;
        this.profitPercentage = profitPercentage;

        this.gstEnabled = gstEnabled;
        this.creditSale = creditSale;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getSaleType() {
        return saleType;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getBillDate() {
        return billDate;
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

    public double getGstTotal() {
        return gstTotal;
    }

    public double getPurchaseTotal() {
        return purchaseTotal;
    }

    public double getProfit() {
        return profit;
    }

    public double getProfitPercentage() {
        return profitPercentage;
    }

    public boolean isGstEnabled() {
        return gstEnabled;
    }

    public boolean isCreditSale() {
        return creditSale;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
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

    public void setGstTotal(double gstTotal) {
        this.gstTotal = gstTotal;
    }

    public void setPurchaseTotal(double purchaseTotal) {
        this.purchaseTotal = purchaseTotal;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setProfitPercentage(double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public void setGstEnabled(boolean gstEnabled) {
        this.gstEnabled = gstEnabled;
    }

    public void setCreditSale(boolean creditSale) {
        this.creditSale = creditSale;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    public String getPaymentStatus() {

        return balanceAmount > 0
                ? "Pending"
                : "Paid";
    }

    public String getGSTType() {

        return gstEnabled
                ? "GST"
                : "Non GST";
    }

    public double getCollectionPercentage() {

        if (grandTotal == 0) {
            return 0;
        }

        return (paidAmount / grandTotal) * 100;
    }

    public double getOutstandingPercentage() {

        if (grandTotal == 0) {
            return 0;
        }

        return (balanceAmount / grandTotal) * 100;
    }

    public double getProfitMargin() {

        if (grandTotal == 0) {
            return 0;
        }

        return (profit / grandTotal) * 100;
    }

    @Override
    public String toString() {

        return "SalesAnalyticsModel{" +
                "invoiceNo='" + invoiceNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", grandTotal=" + grandTotal +
                ", profit=" + profit +
                '}';
    }
}