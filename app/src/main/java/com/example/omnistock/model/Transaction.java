package com.example.omnistock.model;

public class Transaction {
    private int transactId;
    private int userId;
    private double totalAmount;
    private String status;
    private String createdAt;
    private String itemsSummary;

    public Transaction(int transactId, int userId, double totalAmount, String status, String createdAt, String itemsSummary) {
        this.transactId = transactId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.itemsSummary = itemsSummary;
    }

    public int getTransactId() {
        return transactId;
    }

    public void setTransactId(int transactId) {
        this.transactId = transactId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }
}
