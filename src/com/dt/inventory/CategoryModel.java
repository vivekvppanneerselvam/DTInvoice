package com.dt.inventory;

public class CategoryModel {

    private int id;

    private String categoryName;

    public CategoryModel(int id,
                         String categoryName) {

        this.id = id;
        this.categoryName = categoryName;
    }

    public CategoryModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
