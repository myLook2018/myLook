package com.mylook.mylook.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
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
    private TextView mWaiting;
    private LinearLayout mLayout;
    private Button btnLogin;
    private String providerLogin;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Login", "Started on create");
        setupLoginActivity();
        Log.e("Login", "FInish on create");
    }

    @Override
    protected void onResume() {
        Log.e("Login", "Resume act");
        super.onResume();
        setupLoginActivity();
        Log.e("Login", "FInish on resume");
    }

    private void setupLoginActivity() {
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
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private void login() {
        if (validateFields()) {
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            mLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e != null) {
                                    if (task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred.")) {
                                        Toast.makeText(mContext, "Revisa tu conexión a internet",
                                                Toast.LENGTH_SHORT).show();
                                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(mContext, "Contraseña incorrecta",
                                                Toast.LENGTH_SHORT).show();
                                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(mContext, "El Email no existe",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "Algo salió mal :(",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    onResume();
                                }
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
        );
    } else

    {
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
                    db.collection("clients").whereEqualTo("email", user.getEmail())
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
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (user != null && user.isEmailVerified()) {
                                        Intent intent = new Intent(mContext, MyLookActivity.class);
                                        Toast.makeText(mContext, "Bienvenido a myLook!",
                                                Toast.LENGTH_SHORT).show();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("[LoginActivity]   ", "eMail no verificado");
                                        displayMessage("Tu email aún no esta verificado");
                                        FirebaseAuth.getInstance().signOut();
                                        onResume();
                                    }
                                }
                            } else {
                                Log.e("[LoginActivity]   ", task.getException().getMessage());
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
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Login - Mylook", "On activity result");
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.e("Login - Mylook", "Data " + data.toString());
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                Log.e("Login - Mylook", "Get Signed ACcount" + task.getResult().toString());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("TAG", "Google sign in failed - " + e.getMessage());
                mProgressBar.setVisibility(View.GONE);
                // ...
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        mProgressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e("Task", "Succesfull " + task.isSuccessful());
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("Facebook", "signInWithCredential:success - User " + mAuth.getCurrentUser().getDisplayName());
                            user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.GONE);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Facebook", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            user = mAuth.getCurrentUser();
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
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmail = findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mLayout = (LinearLayout) findViewById(R.id.login_form);
        btnLogin = (Button) findViewById(R.id.login_button);
        signUpLink = (TextView) findViewById(R.id.link_signup);
        btnGoogleSign = findViewById(R.id.google_sign_in_button);
        resetPassword = findViewById(R.id.recover_password);
        layout = findViewById(R.id.layout_login);
        loginButton = findViewById(R.id.facebook_login);
    }

    private void getIncomingIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("email")) {
            Log.d("IncomingIntent", "getIncomingIntent: found intent extras.");
            mEmail.setText(intent.getStringExtra("email"));
        }
        if (intent.hasExtra("confirmation")) {
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


