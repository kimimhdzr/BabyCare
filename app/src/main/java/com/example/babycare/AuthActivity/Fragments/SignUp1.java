package com.example.babycare.AuthActivity.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SignUp1 extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    final int RC_SIGN_IN = 100;
    GoogleApiClient mGoogleApiClient;
    Context context;
    public FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView signinTxtView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signinTxtView = view.findViewById(R.id.back_login);


        NavController navController = NavHostFragment.findNavController(this);
        signinTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.stopAutoManage(getActivity());
                mGoogleApiClient.disconnect();
                navController.navigate(R.id.nav_to_Login);
            }
        });


        //Register using Email and Password
        emailEditText = view.findViewById(R.id.input_email_signup);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_pass);
        signUpButton = view.findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(v -> checking());


        ImageButton googleSignIn = view.findViewById(R.id.google_signup);



        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInwithGoogle(mGoogleApiClient);
            }
        });



        return view;
    }

    public void checking() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        if(password.length() <8){
            passwordEditText.setError("Password needs to have at least 8 characters");
            return;
        }

        checkRegisteredEmail(email)
                .addOnSuccessListener(isRegistered -> {
                    if (isRegistered) {
                        emailEditText.setError("Email has already been registered");
                    } else {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signInWithEmailAndPassword(email, password) // Using a dummy password to check if the email is registered
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // If sign-in is successful, the email is registered
                                            emailEditText.setError("Email is already in use");
                                        } else {
                                            // Handle exceptions like if email is not found

                                            // Email is not registered yet, proceed with the next step
                                            Bundle bundle = new Bundle();
                                            bundle.putString("input_email", email);
                                            bundle.putString("input_pass", password);
                                            mGoogleApiClient.stopAutoManage(getActivity());
                                            mGoogleApiClient.disconnect();
                                            // Proceed to the next step (e.g., navigate to SignUp2)
                                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_auth);
                                            navController.navigate(R.id.nav_to_SignUp2, bundle);

                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure (e.g., network errors)
                    System.err.println("Error checking email: " + e.getMessage());
                });








    }

    protected void signInwithGoogle(GoogleApiClient mGoogleApiClient){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            Log.d("TEST","FR" + task.getResult().getAdditionalUserInfo().isNewUser());

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){

                                String UID = mAuth.getCurrentUser().getUid();
                                Bundle bundle = new Bundle();
                                bundle.putString("UID",UID);


                                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_auth);
                                navController.navigate(R.id.nav_to_SignUp2,bundle);
                            }
                            else{
                                Toast.makeText(getActivity(), "Authentication pass.",
                                        Toast.LENGTH_SHORT).show();


                                String UID = mAuth.getCurrentUser().getUid();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra("session_id",UID);
                                startActivity(intent);
                                getActivity().finish();

                            }


                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("TTEST","FR"+result.getStatus());
            if (result.isSuccess()) {

                GoogleSignInAccount acct = result.getSignInAccount();
                ;
                checkRegisteredEmail(acct.getEmail())
                        .addOnSuccessListener(isRegistered -> {
                            if (isRegistered) {
                                Toast.makeText(getActivity(),"Email is already registered",Toast.LENGTH_LONG);
                            } else {
                                firebaseAuthWithGoogle(acct);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure (e.g., network errors)
                            System.err.println("Error checking email: " + e.getMessage());
                        });
                firebaseAuthWithGoogle(acct);
            } else {
                Toast.makeText(getActivity(),"There was a trouble signing in-Please try again",Toast.LENGTH_SHORT).show();;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public Task<Boolean> checkRegisteredEmail(String email) {
        // Check if the email is already associated with any account
        return mAuth.fetchSignInMethodsForEmail(email)
                .continueWith(task -> !task.getResult().getSignInMethods().isEmpty());
    }



}