package com.vogella.android.myapplication.model;

import java.io.Serializable;
import java.util.Date;

public class Farm implements Serializable {

    private int id;
    private int UserId;
    private String FarmName;
    private int Size;
    private String Location;
    private Date DateCreated;
    private String Description;

    public Farm() {}

    public Farm(int userId, String farmName, int size, String location, String description) {
        UserId = userId;
        FarmName = farmName;
        Size = size;
        Location = location;
        DateCreated = new Date();
        Description = description;
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

    public String getFarmName() {
        return FarmName;
    }

    public void setFarmName(String farmName) {
        FarmName = farmName;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
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
        return "Expense ["
                + "id=" + id + ", "
                + "UserId=" + UserId + ", "
                + "FarmName=" + FarmName + ", "
                + "Size=" + Size + ", "
                + "Location=" + Location
                + "]";
    }
}
