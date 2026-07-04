package com.dt.invoice;

public class InvoiceItemModel {

    private int id;

    private int invoiceId;

    private int itemId;

    private String itemName;

    private double quantity;

    private String unit;

    private double price;

    private double gstPercent;

    private double gstAmount;

    private double total;

    private int measurementId;


    public InvoiceItemModel() {
    }

    public int getMeasurementId() {
        return measurementId;
    }



    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }



    public int getId() {
        return id;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public double getGstPercent() {
        return gstPercent;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setGstPercent(double gstPercent) {
        this.gstPercent = gstPercent;
    }

    public void setGstAmount(double gstAmount) {
        this.gstAmount = gstAmount;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}