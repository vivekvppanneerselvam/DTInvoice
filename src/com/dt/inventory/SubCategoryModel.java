package com.dt.inventory;

public class SubCategoryModel {

    private int id;

    private int categoryId;

    private String categoryName;

    private String subCategoryName;

    // =====================================================
    // DEFAULT CONSTRUCTOR
    // =====================================================

    public SubCategoryModel() {

    }

    // =====================================================
    // PARAMETERIZED CONSTRUCTOR
    // =====================================================

    public SubCategoryModel(int id,
                            int categoryId,
                            String categoryName,
                            String subCategoryName) {

        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(int id) {
        this.id = id;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    // =====================================================
    // TOSTRING
    // =====================================================

    @Override
    public String toString() {

        return subCategoryName != null

                ?

                subCategoryName

                :

                "";
    }
}