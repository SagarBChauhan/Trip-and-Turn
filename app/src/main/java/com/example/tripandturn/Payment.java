package com.example.tripandturn;

public class Payment {
    String  amount, mode, account_no, bank_name, bank_ifsc, owner_ac_no, owner_bank_name, owner_ifsc,LastUpdateTime;

    public Payment() {
    }

    public Payment(String amount, String mode, String account_no, String bank_name, String bank_ifsc, String owner_ac_no, String owner_bank_name, String owner_ifsc, String lastUpdateTime) {
        this.amount = amount;
        this.mode = mode;
        this.account_no = account_no;
        this.bank_name = bank_name;
        this.bank_ifsc = bank_ifsc;
        this.owner_ac_no = owner_ac_no;
        this.owner_bank_name = owner_bank_name;
        this.owner_ifsc = owner_ifsc;
        LastUpdateTime = lastUpdateTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_ifsc() {
        return bank_ifsc;
    }

    public void setBank_ifsc(String bank_ifsc) {
        this.bank_ifsc = bank_ifsc;
    }

    public String getOwner_ac_no() {
        return owner_ac_no;
    }

    public void setOwner_ac_no(String owner_ac_no) {
        this.owner_ac_no = owner_ac_no;
    }

    public String getOwner_bank_name() {
        return owner_bank_name;
    }

    public void setOwner_bank_name(String owner_bank_name) {
        this.owner_bank_name = owner_bank_name;
    }

    public String getOwner_ifsc() {
        return owner_ifsc;
    }

    public void setOwner_ifsc(String owner_ifsc) {
        this.owner_ifsc = owner_ifsc;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }
}
