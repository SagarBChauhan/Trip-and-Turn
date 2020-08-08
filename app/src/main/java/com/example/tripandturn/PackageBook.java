package com.example.tripandturn;

public class PackageBook {
    String customer_id,package_id,total_amount,payment_id,
            LastUpdateTime;

    public PackageBook() {
    }

    public PackageBook(String customer_id, String package_id, String total_amount, String payment_id, String lastUpdateTime) {
        this.customer_id = customer_id;
        this.package_id = package_id;
        this.total_amount = total_amount;
        this.payment_id = payment_id;
        LastUpdateTime = lastUpdateTime;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }
}
