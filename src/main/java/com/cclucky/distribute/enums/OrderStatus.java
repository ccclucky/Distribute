package com.cclucky.distribute.enums;

public enum OrderStatus {
    CREATED("Created"),
    SUBMITTED("Submitted"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
