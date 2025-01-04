package com.example.babycareconnect;

public class childItem {

    String babyName;
    String  babyProfilePictureUri;

    public childItem(String babyName,String  babyProfilePictureUri) {
        this.babyName = babyName;
        this.babyProfilePictureUri = babyProfilePictureUri;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public String getBabyProfilePictureUri() {
        return babyProfilePictureUri;
    }

    public void setBabyProfilePicture(String babyProfilePictureUri) {
        this.babyProfilePictureUri = babyProfilePictureUri;
    }
}
