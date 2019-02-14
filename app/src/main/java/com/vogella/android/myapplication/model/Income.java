package com.vogella.android.myapplication.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Income implements Serializable {
    private int id;
    private int UserId;
    private Date IncomeDate;
    private int CustomerId;
    private BigDecimal Amount;
    private int PaymentMethodId;
    private int AccountId;
    private int ProjectId;
    private String Notes;
    private String Customer;
    private String Account;
    private String ProjectName;
    private String PaymentMethod;
    private String FarmName;

    public Income() {}

    public Income(BigDecimal amount, int customerId, int paymentMethodId, int accountId, int projectId, String notes, int userId) {
        IncomeDate = new Date();
        Amount = amount;
        CustomerId = customerId;
        PaymentMethodId = paymentMethodId;
        AccountId = accountId;
        ProjectId = projectId;
        Notes = notes;
        UserId = userId;
    }

    public Income(Date incomeDate, BigDecimal amount, int customerId, int paymentMethodId, int accountId, int projectId, String notes, int userId) {
        IncomeDate = incomeDate;
        Amount = amount;
        CustomerId = customerId;
        PaymentMethodId = paymentMethodId;
        AccountId = accountId;
        ProjectId = projectId;
        Notes = notes;
        UserId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public Date getIncomeDate() {
        return IncomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        IncomeDate = incomeDate;
    }

    public int getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(int customerId) {
        CustomerId = customerId;
    }

    public BigDecimal getAmount() {
        return Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }

    public int getPaymentMethodId() {
        return PaymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        PaymentMethodId = paymentMethodId;
    }

    public int getAccountId() {
        return AccountId;
    }

    public void setAccountId(int accountId) {
        AccountId = accountId;
    }

    public int getProjectId() {
        return ProjectId;
    }

    public void setProjectId(int projectId) {
        ProjectId = projectId;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getFarmName() {
        return FarmName;
    }

    public void setFarmName(String farmName) {
        FarmName = farmName;
    }
}
