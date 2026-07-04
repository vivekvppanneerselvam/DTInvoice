package com.dt.sales;

public class BusinessInsightModel {

    private String insightType;

    private String title;

    private String description;

    private String severity;

    private String icon;

    public BusinessInsightModel(
            String insightType,
            String title,
            String description,
            String severity,
            String icon) {

        this.insightType = insightType;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.icon = icon;
    }

    public String getInsightType() {
        return insightType;
    }

    public void setInsightType(String insightType) {
        this.insightType = insightType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isCritical() {
        return "CRITICAL".equalsIgnoreCase(severity);
    }

    public boolean isWarning() {
        return "WARNING".equalsIgnoreCase(severity);
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(severity);
    }

    @Override
    public String toString() {

        return icon + " " + title;
    }
}