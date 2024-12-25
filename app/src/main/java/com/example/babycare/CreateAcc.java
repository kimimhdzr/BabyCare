package com.example.babycare;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.security.SecureRandom;
import java.util.Base64;

;


public class CreateAcc extends Fragment {
    final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    Context context;


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

}
