package com.example.babycare.Objects;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Baby implements Serializable {
    String parent;
    String name,bloodtype,birthday;
    int month,week,years;
    ArrayList<String> allergies;


    public Baby(String parent,String name, String bloodtype,String birthday){
        this.parent = parent;
        this.name = name;
        this.bloodtype = bloodtype;
        this.birthday = birthday;
        allergies = new ArrayList<>();
    }

    public String getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getBloodtype() {
        return bloodtype;
    }

    public int getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public int getYears() {
        return years;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYears(int years){
        this.years = years;
    }

    public void addAllergy(String allergy){
        allergies.add(allergy);
    }
}
