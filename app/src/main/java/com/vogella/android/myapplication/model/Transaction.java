package com.vogella.android.myapplication.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction implements Comparable<Transaction> {

    private int transactionID;
    private BigDecimal transactionAmount;
    private Date transactionDate;
    private String transactionDescription;
    private String transactionType;

    public Transaction() {}

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public int compareTo(Transaction u) {
        if (getTransactionDate() == null ) {
            return 0;
        }

        if ( u.getTransactionDate() == null) {
            return 0;
        }
        return getTransactionDate().compareTo(u.getTransactionDate());
    }
}
