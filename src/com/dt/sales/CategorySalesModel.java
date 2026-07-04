package com.dt.sales;

public class CategorySalesModel {

    private String categoryName;

    private double salesAmount;

    private int itemCount;

    private double percentage;

    public CategorySalesModel(
            String categoryName,
            double salesAmount,
            int itemCount,
            double percentage) {

        this.categoryName = categoryName;
        this.salesAmount = salesAmount;
        this.itemCount = itemCount;
        this.percentage = percentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getSalesAmount() {
        return salesAmount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public double getPercentage() {
        return percentage;
    }
}