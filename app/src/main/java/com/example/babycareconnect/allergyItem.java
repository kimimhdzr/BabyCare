package com.example.babycareconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class allergyItem {

    String allergyInput;

    public allergyItem(String allergyInput){
        this.allergyInput=allergyInput;
    }

    public allergyItem(){
        this.allergyInput="";
    }

    public String getAllergyInput() {
        return allergyInput;
    }

    public void setAllergyInput(String allergyInput) {
        this.allergyInput = allergyInput;
    }
}
