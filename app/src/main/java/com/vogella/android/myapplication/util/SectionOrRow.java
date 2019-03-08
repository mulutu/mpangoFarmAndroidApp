package com.vogella.android.myapplication.util;

public class SectionOrRow {
    private int id;
    private String row;
    private String row2;
    private String section;
    private boolean isRow;

    public static SectionOrRow createRow(int id, String row, String row2) {
        SectionOrRow ret = new SectionOrRow();
        ret.id =  id;
        ret.row = row;
        ret.row2 = row2;
        ret.isRow = true;
        return ret;
    }

    public static SectionOrRow createSection(String section) {
        SectionOrRow ret = new SectionOrRow();
        ret.section = section;
        ret.isRow = false;
        return ret;
    }

    public int getId(){
        return id;
    }

    public String getRow() {
        return row;
    }

    public String getRow2() {
        return row2;
    }

    public String getSection() {
        return section;
    }

    public boolean isRow() {
        return isRow;
    }
}
