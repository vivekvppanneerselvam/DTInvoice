package com.dt.customer;

public class CustomerLedgerModel {

    private int id;

    private int customerId;

    private String entryDate;

    private String entryType;

    private String refNo;

    private double debit;

    private double credit;

    private double balance;

    private String remarks;

    public CustomerLedgerModel(
            int id,
            int customerId,
            String entryDate,
            String entryType,
            String refNo,
            double debit,
            double credit,
            double balance,
            String remarks) {

        this.id = id;
        this.customerId = customerId;
        this.entryDate = entryDate;
        this.entryType = entryType;
        this.refNo = refNo;
        this.debit = debit;
        this.credit = credit;
        this.balance = balance;
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getEntryType() {
        return entryType;
    }

    public String getRefNo() {
        return refNo;
    }

    public double getDebit() {
        return debit;
    }

    public double getCredit() {
        return credit;
    }

    public double getBalance() {
        return balance;
    }

    public String getRemarks() {
        return remarks;
    }
}