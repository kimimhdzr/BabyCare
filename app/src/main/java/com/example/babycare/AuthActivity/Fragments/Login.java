package com.example.babycare.AuthActivity.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babycare.MainActivity.Fragments.Home.Home;
import com.example.babycare.MainActivity.MainActivity;
import com.example.babycare.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TotpMultiFactorAssertion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Login extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;
    private EditText email, password;
    private Button loginButton, signupButton;
    private TextView forgotpassTxtView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.sign_in);
        signupButton = view.findViewById(R.id.create_account);
        forgotpassTxtView = view.findViewById(R.id.forgot_password);

        //FacebookSdk.setApplicationId("955825376045264");
        //FacebookSdk.sdkInitialize(requireContext());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this::onConnectionFailed)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        NavController navController = NavHostFragment.findNavController(this);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("gso", gso);
                mGoogleApiClient.stopAutoManage(getActivity());
                mGoogleApiClient.disconnect();

                navController.navigate(R.id.nav_to_SignUp1,bundle);
            }
        });
        forgotpassTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.nav_to_ForgotPassword);
            }
        });


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (TextUtils.isEmpty(emailText)) {
                email.setError("Email is required");
                return;
            }

            if (TextUtils.isEmpty(passwordText)) {
                password.setError("Password is required");
                return;
            }

            // Sign in with Firebase
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                            String UID = mAuth.getCurrentUser().getUid();

                            Log.d("TEST","WHAT" + UID);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("session_id",UID);
                            startActivity(intent);
                            getActivity().finish();
                            // Navigate to another activity (e.g., MainActivity)
                        } else {
                            Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });




        ImageButton googleSignIn = view.findViewById(R.id.google_signin);


        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInwithGoogle(mGoogleApiClient);
            }
        });

        //FACEBOOK SIGNIN

        //mCallbackManager = CallbackManager.Factory.create();
        //LoginButton signinFacebook = view.findViewById(R.id.facebook_signin);
        //signinFacebook.setReadPermissions("email","public_profile");
        //signinFacebook.setFragment(this);
        //.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            //@Override
            //public void onSuccess(LoginResult loginResult) {
                //handleFacebookAccessToken(loginResult.getAccessToken());
            //}

       // });


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

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
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
                firebaseAuthWithGoogle(acct);
            } else {
                Toast.makeText(getActivity(),"There was a trouble signing in-Please try again",Toast.LENGTH_SHORT).show();;
            }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}