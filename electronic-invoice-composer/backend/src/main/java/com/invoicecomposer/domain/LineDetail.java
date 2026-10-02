package com.invoicecomposer.domain;

/**
 * Represents a single line in the invoice financial breakdown.
 * Each decorator that modifies totals appends a LineDetail to the breakdown.
 */
public class LineDetail {

    private String type;
    private String label;
    private String description;
    private double amount;
    private double rate;
    private double baseAmount;

    public LineDetail() {
    }

    public LineDetail(String type, String label, String description,
                      double amount, double rate, double baseAmount) {
        this.type = type;
        this.label = label;
        this.description = description;
        this.amount = amount;
        this.rate = rate;
        this.baseAmount = baseAmount;
    }

    // --- Getters and Setters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }
}
