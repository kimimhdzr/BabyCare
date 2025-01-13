package com.example.babycare.DataBinding.Model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BabyProfileModel implements Serializable {

    String documentID;
    String name;
    String dob;
    String height;
    String weight;
    String bloodType;
    private Map<String, Object> parent = new HashMap<>();
    String allergies;
    String profilePic;
    int month;
    int week;

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    int years;

    public BabyProfileModel(
            String documentID,
            String name,
            String dob,
            String height,
            String weight,
            String bloodType,
            String fatherId,
            String motherId,
            String guardianId,
            String allergies,
            String profilePic
    ) {
        this.documentID = documentID;
        this.name = name;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        parent.put("fatherId", fatherId);
        parent.put("motherId", motherId);
        parent.put("guardianId", guardianId);
        this.allergies = allergies;
        this.profilePic = profilePic;
    }


    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Map<String, Object> getParent() {
        return parent;
    }

    public void setParent(Map<String, Object> parent) {
        this.parent = parent;
    }

    public ArrayList<String> getAllergies() {
        if (allergies != null && !allergies.isEmpty()) {
            // Split the string by commas and convert it into an ArrayList
            String[] allergyArray = allergies.split(",");
            ArrayList<String> allergyList = new ArrayList<>();

            for (String allergy : allergyArray) {
                // Trim whitespace and add to the list
                allergyList.add(allergy.trim());
            }

            return allergyList;
        }
        // Return an empty list if the allergies string is null or empty
        return new ArrayList<>();
    }

    public void setAllergies(ArrayList<String> allergiesList) {
        if (allergiesList != null && !allergiesList.isEmpty()) {
            // Convert the ArrayList into a comma-separated string
            this.allergies = String.join(", ", allergiesList);
        } else {
            // Set to null or empty string if the list is null or empty
            this.allergies = "";
        }
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void removeAllergy(int position) {
        if (allergies == null || allergies.isEmpty()) {
            System.out.println("No allergies to remove.");
            return;
        }

        // Convert the allergies string to an ArrayList
        List<String> allergyList = new ArrayList<>(Arrays.asList(allergies.split(", ")));

        // Check if the position is valid
        if (position >= 0 && position < allergyList.size()) {
            // Remove the allergy at the specified position
            allergyList.remove(position);

            // Convert the updated list back to a string
            this.allergies = String.join(", ", allergyList);
        } else {
            System.out.println("Invalid position: " + position);
        }
    }

    public void addAllergy(String newAllergy) {
        if (newAllergy == null || newAllergy.trim().isEmpty()) {
            System.out.println("Invalid allergy provided.");
            return;
        }

        // If the allergies field is empty, initialize it with the new allergy
        if (allergies == null || allergies.isEmpty()) {
            this.allergies = newAllergy.trim();
        } else {
            // Convert the allergies string to an ArrayList
            List<String> allergyList = new ArrayList<>(Arrays.asList(allergies.split(", ")));

            // Check if the allergy already exists to avoid duplication
            if (!allergyList.contains(newAllergy.trim())) {
                // Add the new allergy
                allergyList.add(newAllergy.trim());

                // Convert the updated list back to a string
                this.allergies = String.join(", ", allergyList);
            } else {
                System.out.println("Allergy already exists: " + newAllergy);
            }
        }
    }


}
