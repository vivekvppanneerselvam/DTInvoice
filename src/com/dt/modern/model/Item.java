package com.dt.modern.model;

public class Item {

    private String name;

    private double rate;

    private String category;

    private double gst;

    public Item(String name,
                double rate,
                String category,
                double gst) {

        this.name = name;
        this.rate = rate;
        this.category = category;
        this.gst = gst;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public String getCategory() {
        return category;
    }

    public double getGst() {
        return gst;
    }
}