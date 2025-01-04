package com.example.babycareconnect;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link manage_child#newInstance} factory method to
 * create an instance of this fragment.
 */
public class manage_child extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public manage_child() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment manage_child.
     */
    // TODO: Rename and change types and number of parameters
    public static manage_child newInstance(String param1, String param2) {
        manage_child fragment = new manage_child();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_manage_children, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView= view.findViewById(R.id.addChildRV);

        List<childItem> childList = loadChildrenFromJson();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ManageChildAdapter(getContext(),childList));

        Button addChildBtn= view.findViewById(R.id.AddChildBtn);
        addChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.addBabyDest);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        List<childItem> childList=loadChildrenFromJson();
        RecyclerView recyclerView=getView().findViewById(R.id.addChildRV);
        recyclerView.setAdapter(new ManageChildAdapter(getContext(),childList));
    }

    private List<childItem> loadChildrenFromJson(){
        List<childItem> childList= new ArrayList<>();
        try{
            File file=new File(getContext().getFilesDir(),"children_details.json");
            if(!file.exists()){
                return childList;
            }

            FileInputStream fis=new FileInputStream(file);
            byte[] data=new byte[(int)file.length()];
            fis.read(data);
            fis.close();

            String json=new String(data, "UTF-8");
            JSONArray jsonArray= new JSONArray(json);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String username=jsonObject.getString("username");
                String profileImageUri=jsonObject.optString("profile_image","");

                childList.add(new childItem(username,profileImageUri));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childList;
    }
}