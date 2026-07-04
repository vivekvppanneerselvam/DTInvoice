package com.dt.inventory;

public class ItemModel {

    private int id;

    private String itemCode;

    private String itemName;

    private int categoryId;

    private int subCategoryId;

    private String categoryName;

    private String subCategoryName;

    private String imagePath;

    private boolean gstEnabled;

    private double gstPercentage;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public ItemModel() {

    }

    // =====================================================
    // PARAMETERIZED CONSTRUCTOR
    // =====================================================

    public ItemModel(int id,
                     String itemCode,
                     String itemName,
                     int categoryId,
                     int subCategoryId,
                     String categoryName,
                     String subCategoryName,
                     String imagePath,
                     boolean gstEnabled,
                     double gstPercentage) {

        this.id = id;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.imagePath = imagePath;
        this.gstEnabled = gstEnabled;
        this.gstPercentage = gstPercentage;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public boolean isGstEnabled() {
        return gstEnabled;
    }

    public double getGstPercentage() {
        return gstPercentage;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setGstEnabled(boolean gstEnabled) {
        this.gstEnabled = gstEnabled;
    }

    public void setGstPercentage(double gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    // =====================================================
    // TOSTRING
    // =====================================================

    @Override
    public String toString() {

        return itemName != null
                ? itemName
                : "";
    }
}