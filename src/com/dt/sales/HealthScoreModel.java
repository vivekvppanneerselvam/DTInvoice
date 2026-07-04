package com.dt.sales;

public class HealthScoreModel {

    private double salesScore;

    private double profitScore;

    private double collectionScore;

    private double expenseScore;

    private double overallScore;

    private String status;

    public HealthScoreModel(
            double salesScore,
            double profitScore,
            double collectionScore,
            double expenseScore,
            double overallScore,
            String status) {

        this.salesScore = salesScore;
        this.profitScore = profitScore;
        this.collectionScore = collectionScore;
        this.expenseScore = expenseScore;
        this.overallScore = overallScore;
        this.status = status;
    }

    public double getSalesScore() {
        return salesScore;
    }

    public double getProfitScore() {
        return profitScore;
    }

    public double getCollectionScore() {
        return collectionScore;
    }

    public double getExpenseScore() {
        return expenseScore;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public String getStatus() {
        return status;
    }
}