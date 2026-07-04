package com.dt.modern.model;

public class CartItem {

    // =====================================================
    // ITEM INFO
    // =====================================================

    private int itemId;

    private int measurementId;

    private String itemName;

    private String measurementName;

    private String unit;

    // =====================================================
    // BILLING
    // =====================================================

    private double qty;

    private double rate;

    private double gst;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public CartItem() {

    }

    // =====================================================
    // PARAMETERIZED CONSTRUCTOR
    // =====================================================

    public CartItem(int itemId,
                    int measurementId,
                    String itemName,
                    String measurementName,
                    String unit,
                    double qty,
                    double rate,
                    double gst) {

        this.itemId = itemId;
        this.measurementId = measurementId;
        this.itemName = itemName;
        this.measurementName = measurementName;
        this.unit = unit;
        this.qty = qty;
        this.rate = rate;
        this.gst = gst;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getItemId() {
        return itemId;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public String getUnit() {
        return unit;
    }

    public double getQty() {
        return qty;
    }

    public double getRate() {
        return rate;
    }

    public double getGst() {
        return gst;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    public double getSubtotal() {

        return qty * rate;
    }

    public double getGSTAmount() {

        return getSubtotal() * gst / 100;
    }

    public double getTotal() {

        return getSubtotal() + getGSTAmount();
    }

    // =====================================================
    // DISPLAY
    // =====================================================

    @Override
    public String toString() {

        return itemName

                +

                " "

                +

                measurementName;
    }
}