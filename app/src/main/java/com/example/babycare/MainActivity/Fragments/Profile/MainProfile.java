package com.example.babycare.MainActivity.Fragments.Profile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.SharedUserModel;
import com.example.babycare.Objects.User;
import com.example.babycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import org.w3c.dom.Text;

public class MainProfile extends Fragment {
    User session_user;

    SharedUserModel sharedUserModel;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUserModel = new ViewModelProvider(requireActivity()).get(SharedUserModel.class);
        session_user = (User) sharedUserModel.getSharedData().getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_profile, container, false);

        TextView parents_name = view.findViewById(R.id.parent_profile_name);
        parents_name.setText(session_user.getUsername());

        LinearLayout manage_children_btn = view.findViewById(R.id.profile_manage_btn);
        LinearLayout setting_btn = view.findViewById(R.id.profile_setting_btn);
        LinearLayout edit_btn = view.findViewById(R.id.profile_edit_btn);

        manage_children_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_to_ManageChildren);
            }
        });



        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_to_Setting);
            }
        });
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean googleUser = CheckIfGoogleProvider();
                if(googleUser){
                    Toast.makeText(getContext(),"Google User",Toast.LENGTH_LONG).show();

                }
                else{
                    Navigation.findNavController(view).navigate(R.id.nav_to_EditProfile2);
                }
            }
        });




        return view;
    }

    public boolean CheckIfGoogleProvider(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            for (UserInfo profile : user.getProviderData()) {
                String providerId = profile.getProviderId();
                if ("google.com".equals(providerId)) {
                    return true;
                }
            }


        return false;
    }
}