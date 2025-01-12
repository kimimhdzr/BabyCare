package com.example.babycare.Objects;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Baby implements Serializable {
    String ID;
    String parent;
    String name,bloodtype,birthday;
    int month,week,years;
    String height,weight;
    ArrayList<String> allergies;

    String imageURI;

    long time;


    public Baby(String parent,String name, String bloodtype,String birthday,String height,String weight){
        this.parent = parent;
        this.name = name;
        this.bloodtype = bloodtype;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        allergies = new ArrayList<>();
    }

    public String getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
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

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void addAllergy(String allergy){
        allergies.add(allergy);
    }

    public void setAllergies(ArrayList<String> allergies) {
        this.allergies = allergies;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public ArrayList<String> getAllergies(){
        return allergies;
    }

    public void removeAllergy(int position){
        allergies.remove(position);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
