package com.vogella.android.myapplication.model;

public class Account {
    private int id;
    private String AccountName;
    private String AccountCode;
    private long AccountTypeId;
    private String description;
    private long userId;
    private String accountTypeCode;
    private String accountTypeName;
    private String accountGroupTypeCode;
    private int accountGroupTypeId;
    private String accountGroupTypeName;

    public Account() {
        //id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getAccountCode() {
        return AccountCode;
    }

    public void setAccountCode(String accountCode) {
        AccountCode = accountCode;
    }

    public long getAccountTypeId() {
        return AccountTypeId;
    }

    public void setAccountTypeId(long accountTypeId) {
        AccountTypeId = accountTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public String getAccountGroupTypeCode() {
        return accountGroupTypeCode;
    }

    public void setAccountGroupTypeCode(String accountGroupTypeCode) {
        this.accountGroupTypeCode = accountGroupTypeCode;
    }

    public int getAccountGroupTypeId() {
        return accountGroupTypeId;
    }

    public void setAccountGroupTypeId(int accountGroupTypeId) {
        this.accountGroupTypeId = accountGroupTypeId;
    }

    public String getAccountGroupTypeName() {
        return accountGroupTypeName;
    }

    public void setAccountGroupTypeName(String accountGroupTypeName) {
        this.accountGroupTypeName = accountGroupTypeName;
    }
}
