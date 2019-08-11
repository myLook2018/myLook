package com.mylook.mylook.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.home.MyLookActivity;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mPassword;
    private AutoCompleteTextView mEmail;
    private LinearLayout mLayout;
    private Button btnLogin;
    private String providerLogin;
    private SignInButton btnGoogleSign;
    private TextView signUpLink, resetPassword;
    private FirebaseUser user;
    private int RC_SIGN_IN = 5;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private ConstraintLayout layout;
    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    private String LOG_LABEL="[LOGIN]";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_LABEL, "Started on create");
        setupLoginActivity();
        Log.e(LOG_LABEL, "Finish on create");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(LOG_LABEL, "Resume act");
        setupLoginActivity();
        Log.e(LOG_LABEL, "FInish on resume");
    }


    private void setupLoginActivity() {
        Log.e(LOG_LABEL, "Iniciando Setup Activity");
        setContentView(R.layout.activity_login);
        initElements();
        mContext = LoginActivity.this;
        mProgressBar.setVisibility(View.GONE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setupFirebaseAuth();
        setupFacebookAuth();
        getIncomingIntent();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnGoogleSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ResetPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Log.e(LOG_LABEL, "Finaliza Setup Activity");
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private void login() {
        Log.e(LOG_LABEL, "Iniciando login ");
        if (validateFields()) {
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            mLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            Log.e(LOG_LABEL, "Login con email y password");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Exception e = task.getException();
                                        if (e != null) {
                                            if (task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                                displayMessage("Revisa tu conexión a internet");
                                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                displayMessage("Contraseña incorrecta");
                                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                                displayMessage("El Email no existe");
                                            } else {
                                                displayMessage("Algo salió mal :(");
                                                Log.e("Login Faild: ", e.getMessage());
                                            }
                                            onResume();
                                        }
                                    }
                                    mProgressBar.setVisibility(View.GONE);
                                }

                            }
                    );
        } else {
            Log.e(LOG_LABEL, "Campos no correctos");
            if (isLoggedIn()) {
                Log.e(LOG_LABEL, "Access Token != Null");
                mProgressBar.setVisibility(View.VISIBLE);
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                mAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken()))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(LOG_LABEL, "Login con credentials");
                                user = mAuth.getCurrentUser();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }

    }

    private boolean validateFields() {
        if (isStringNull(mEmail.getText().toString())) {
            if (!isLoggedIn())
                displayMessage("El campo Email es obligatorio");
            return false;
        }
        if (isStringNull(mPassword.getText().toString())) {
            if (!isLoggedIn())
                displayMessage("Debes ingresar una contraseña");
            return false;
        }
        return true;
    }

    private void googleSignIn() {
        providerLogin = "google";
        mProgressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mAuth.getCurrentUser(); //firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(LOG_LABEL, "Buscando user en Firebase");
                    FirebaseFirestore.getInstance().collection("clients").whereEqualTo("email", user.getEmail())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() == 0) { //esto deberia pasar a la validacion del mail si existe o no
                                    Intent intent = new Intent(mContext, RegisterActivity.class);
                                    CharSequence mail = user.getEmail();
                                    CharSequence name = user.getDisplayName();
                                    intent.putExtra("mail", mail);
                                    intent.putExtra("displayName", name);
                                    intent.putExtra("provider", providerLogin);
                                } else {
                                    if (user != null && user.isEmailVerified()) {
                                        Intent intent = new Intent(mContext, MyLookActivity.class);
                                        displayMessage("Bienvenido a myLook!");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(LOG_LABEL, "email No verificado");
                                        FirebaseAuth.getInstance().signOut();
                                        try {
                                            onResume();
                                        } catch (Exception e) {
                                            Log.e(LOG_LABEL,"Login ex: "+ e.getMessage());
                                        }

                                    }
                                }
                            } else {
                                Log.e(LOG_LABEL, "Busqueda en firebase fallo"+ task.getException());
                            }
                        }
                    });
                }
            }
        };
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void setupFacebookAuth() {
        if (isLoggedIn()) {
            mProgressBar.setVisibility(View.VISIBLE);
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            mAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken()))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
        final String TAG = "Facebook";
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(LOG_LABEL, TAG+" facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(LOG_LABEL, TAG+" facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(LOG_LABEL, TAG+" facebook:onError", error);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(LOG_LABEL, "On Activity result, iniciando");
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.e(LOG_LABEL, "On Activity result, data: "+data.toString());
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                Log.e(LOG_LABEL, "On Activity result, Get Signed account: "+task.getResult().toString());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(LOG_LABEL, "On Activity result, Google sign in failed: "+e.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        }
        Log.e(LOG_LABEL, "On Activity result, fin");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(LOG_LABEL, " Handle Facebook Access Token, inicio");
        mProgressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(LOG_LABEL, " Handle Facebook Access Token, Succesfull: " + task.isSuccessful());
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(LOG_LABEL, " Handle Facebook Access Token, " +
                                    "signInWithCredential:success - User " + mAuth.getCurrentUser().getDisplayName());
                            user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.GONE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(LOG_LABEL, " Handle Facebook Access Token, +" +
                                    "signInWithCredential:failure", task.getException());
                            displayMessage("Authentication failed.");
                            if (user != null) {
                                FirebaseAuth.getInstance().signOut();
                                finish();
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            displayMessage("No se pudo autenticar con Google");
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void initElements() {
        mProgressBar = findViewById(R.id.progressBar);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mLayout = findViewById(R.id.login_form);
        btnLogin = findViewById(R.id.login_button);
        signUpLink = findViewById(R.id.link_signup);
        btnGoogleSign = findViewById(R.id.google_sign_in_button);
        resetPassword = findViewById(R.id.recover_password);
        layout = findViewById(R.id.layout_login);
        loginButton = findViewById(R.id.facebook_login);
    }

    private void getIncomingIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("email")) {
            Log.e(LOG_LABEL, " Get Incoming Intent, getIncomingIntent: found intent extras, email");
            mEmail.setText(intent.getStringExtra("email"));
        }
        if (intent.hasExtra("confirmation")) {
            Log.e(LOG_LABEL, " Get Incoming Intent, getIncomingIntent: found intent extras, confirmation");
            if (intent.getBooleanExtra("confirmation", false)) {
                Snackbar mySnackbar = Snackbar.make(layout, "Te envíamos un mail para confirmar el registro", Snackbar.LENGTH_LONG);
                mySnackbar.setActionTextColor(getResources().getColor(R.color.accent));
                mySnackbar.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


