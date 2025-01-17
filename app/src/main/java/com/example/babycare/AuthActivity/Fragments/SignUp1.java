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
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SignUp1 extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    Context context;
    public FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private TextView signinTxtView;
    GoogleApiClient mGoogleApiClient;
    LinearLayout googleSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up1, container, false);

        signinTxtView = view.findViewById(R.id.back_login);


        NavController navController = NavHostFragment.findNavController(this);
        signinTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_Login);
            }
        });

//
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
//
//
//        AppCompatImageButton GoogleSignUp = view.findViewById(R.id.google_signup);
//        GoogleSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signUpGoogle();
//            }
//        });


        //Register using Email and Password
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_pass);
        signUpButton = view.findViewById(R.id.sign_up);
        mAuth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(v -> checking());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignIn = view.findViewById(R.id.google_signup);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInwithGoogle(mGoogleApiClient);
            }
        });

        return view;
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

                Log.d("NEWUSER","TEST"+acct.getEmail());
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

            } else {
                Toast.makeText(getActivity(),"There was a trouble signing in-Please try again",Toast.LENGTH_SHORT).show();;
            }
        }
    }
    public Task<Boolean> checkRegisteredEmail(String email) {
        // Check if the email is already associated with any account
        return mAuth.fetchSignInMethodsForEmail(email)
                .continueWith(task -> !task.getResult().getSignInMethods().isEmpty());
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                Log.d("LAUNCH", "AFFIRM");
                if (result.getResultCode() == RESULT_OK) {
                    Log.d("TEST", "SUCESSS");
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                } else {
                    Log.d("FAILURE", "MASLAAH");
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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            // Check if the email is already in use
                            SignInMethodQueryResult result = task.getResult();
                            if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                                emailEditText.setError("Email is already in use");
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("input_email", email);
                                bundle.putString("input_password", confirmPassword);

                                NavController navController = NavHostFragment.findNavController(getParentFragment());
                                navController.navigate(R.id.nav_to_SignUp2, bundle);
                            }
                        } else {
                            // Handle errors
                            Log.e("FirebaseAuth", "Error checking email", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}