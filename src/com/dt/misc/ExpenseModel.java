package com.dt.misc;

public class ExpenseModel {

    private int id;

    private String expenseNo;

    private String expenseDate;

    private String expenseType;

    private String vendorName;

    private double amount;

    private String remarks;

    private String attachmentPath;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ExpenseModel(
            int id,
            String expenseNo,
            String expenseDate,
            String expenseType,
            String vendorName,
            double amount,
            String remarks,
            String attachmentPath) {

        this.id = id;
        this.expenseNo = expenseNo;
        this.expenseDate = expenseDate;
        this.expenseType = expenseType;
        this.vendorName = vendorName;
        this.amount = amount;
        this.remarks = remarks;
        this.attachmentPath = attachmentPath;
    }

    // =====================================================
    // ID
    // =====================================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // =====================================================
    // EXPENSE NO
    // =====================================================

    public String getExpenseNo() {
        return expenseNo;
    }

    public void setExpenseNo(String expenseNo) {
        this.expenseNo = expenseNo;
    }

    // =====================================================
    // EXPENSE DATE
    // =====================================================

    public String getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    // =====================================================
    // EXPENSE TYPE
    // =====================================================

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    // =====================================================
    // VENDOR NAME
    // =====================================================

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    // =====================================================
    // AMOUNT
    // =====================================================

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // =====================================================
    // REMARKS
    // =====================================================

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // =====================================================
    // ATTACHMENT
    // =====================================================

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    public boolean hasAttachment() {

        return attachmentPath != null
                && !attachmentPath.isBlank();
    }

    @Override
    public String toString() {

        return expenseNo
                + " - "
                + expenseType
                + " - ₹"
                + amount;
    }
}
