package com.vogella.android.myapplication.model;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable {
    private int id;
    private int UserId;
    private int FarmId;
    private String ProjectName;
    private Date DateCreated;
    private int expectedOutput;
    private int actualOutput;
    private int unitId;
    private String unitDescription;
    private String Description;

    public Project() {
        //id = 0;
    }

    public Project(int userId, int farmId, String projectName, String description) {
        UserId = userId;
        FarmId = farmId;
        ProjectName = projectName;
        Description = description;
        DateCreated = new Date();
    }




    public String getUnitDescription() {
        return unitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }

    public int getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(int expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public int getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(int actualOutput) {
        this.actualOutput = actualOutput;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFarmId() {
        return FarmId;
    }

    public void setFarmId(int farmId) {
        FarmId = farmId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public Date getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        DateCreated = dateCreated;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public String toString() {
        return "Expense [id=" + id + ", UserId=" + UserId + ", ProjectName=" + ProjectName + ", DateCreated="
                + DateCreated + ", Description=" + Description + "]";
    }
}
