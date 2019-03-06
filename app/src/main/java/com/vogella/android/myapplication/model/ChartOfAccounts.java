package com.vogella.android.myapplication.model;

import java.util.List;

public class ChartOfAccounts {
    private String AccountGroupTypeName;
    private String AccountGroupTypeCode;
    private int AccountGroupTypeId;
    private List<Account> listOfAccounts;

    public ChartOfAccounts() {
    }

    public String getAccountGroupTypeName() {
        return AccountGroupTypeName;
    }

    public void setAccountGroupTypeName(String accountGroupTypeName) {
        AccountGroupTypeName = accountGroupTypeName;
    }

    public String getAccountGroupTypeCode() {
        return AccountGroupTypeCode;
    }

    public void setAccountGroupTypeCode(String accountGroupTypeCode) {
        AccountGroupTypeCode = accountGroupTypeCode;
    }

    public int getAccountGroupTypeId() {
        return AccountGroupTypeId;
    }

    public void setAccountGroupTypeId(int accountGroupTypeId) {
        AccountGroupTypeId = accountGroupTypeId;
    }

    public List<Account> getListOfAccounts() {
        return listOfAccounts;
    }

    public void setListOfAccounts(List<Account> listOfAccounts) {
        this.listOfAccounts = listOfAccounts;
    }
}
