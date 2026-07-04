package com.dt.inventory;

public class ItemMeasurement {

    private int id;

    private int itemId;

    private double quantity;

    private String unit;

    private double sellingPrice;

    private double purchasePrice;

    private double currentStock;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public ItemMeasurement() {

    }

    // =====================================================
    // PARAMETERIZED CONSTRUCTOR
    // =====================================================

    public ItemMeasurement(int id,
                           int itemId,
                           double quantity,
                           String unit,
                           double sellingPrice,
                           double purchasePrice,
                           double currentStock) {

        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unit = unit;
        this.sellingPrice = sellingPrice;
        this.purchasePrice = purchasePrice;
        this.currentStock = currentStock;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public double getCurrentStock() {
        return currentStock;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }

    // =====================================================
    // TOSTRING
    // =====================================================

    @Override
    public String toString() {

        return quantity + " " + unit;
    }
}