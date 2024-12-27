package com.example.babycare;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.security.SecureRandom;
import java.util.Base64;

;


public class CreateAcc extends Fragment {
    final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    Context context;

    public FirebaseAuth FAuth;
    public EditText input_email, input_password, input_conf;
    public Button SignUp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.create_account, container, false);
        context = getContext();

        Button backtoLogin = view.findViewById(R.id.back_login);
        backtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);



        AppCompatImageButton GoogleSignUp = view.findViewById(R.id.google_signup);
        GoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpGoogle();
            }
        });




        //Register using Email and Password
        input_email = view.findViewById(R.id.email);
        input_password = view.findViewById(R.id.password);
        input_conf = view.findViewById(R.id.confirm_pass);
        SignUp = view.findViewById(R.id.sign_up);
        FAuth = FirebaseAuth.getInstance();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean PassValid = verifyPass();

                String email = String.valueOf(input_email.getText());
                String pass = String.valueOf(input_conf.getText());

                if(PassValid && !email.isEmpty() &&!pass.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
                    Bundle bundle = new Bundle();
                    bundle.putString("input_email", email);
                    bundle.putString("input_password", pass);

                    Fragment newFrag = new CompleteAcc();
                    newFrag.setArguments(bundle);

                    // Assuming this is inside a Fragment
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .addToBackStack(null)
                            .commit();

                }else{
                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        input_email.setError("Valid email required");
                        input_email.requestFocus();
                        return;
                    }

                    if (pass.isEmpty() || pass.length() < 6) {
                        input_password.setError("Password must be at least 6 characters");
                        input_password.requestFocus();
                        return;
                    }
                }
            }
        });




        return view;
    }

    private void signUpGoogle() {
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            Log.d("PREPARE","LAUNCHING");
            signInLauncher.launch(signInIntent);
            Log.d("LAUNCH","AFFIRM");
        }catch (Exception e){
            Log.d("ERROR","ENTAH");
        }

    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Log.d("LAUNCH","AFFIRM");
        if (result.getResultCode() == RESULT_OK) {
            Log.d("TEST","SUCESSS");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            handleSignInResult(task);
        }else{
            Log.d("FAILURE","MASLAAH");
        }
    });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
            String idToken = account.getIdToken();
            String email = account.getEmail();
            String displayName = account.getDisplayName();
            Log.w("TEST",email);
        } catch (ApiException e) {
            Log.w("FRF", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account)
    {
        if (account != null) {
            String displayName = account.getDisplayName();
            Log.d("FRF","TE" + displayName);
            String email = account.getEmail();
            Log.d("FRF","TE" + email);
        }else{
            Log.d("NOTHING","NULL");
        }
    }


    private boolean verifyPass(){
        String pass = String.valueOf(input_password.getText());
        String conf = String.valueOf(input_conf.getText());

        if(!pass.equals(conf)){
            Toast.makeText(getContext(),"Password do no match",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }




}
