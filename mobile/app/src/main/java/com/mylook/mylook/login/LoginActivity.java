package com.mylook.mylook.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mylook.mylook.R;
import com.mylook.mylook.home.HomeActivity;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mWaiting;
    private LinearLayout mLayout;
    private Button btnLogin;
    private TextView signUpLink;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initElements();
        mContext = LoginActivity.this;
        mProgressBar.setVisibility(View.GONE);
        FirebaseAuth.getInstance().signOut();
        setupFirebaseAuth();
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
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private void login(){
        if(validateFields()){
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            mLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                            } else {
                                if(task.getException().getMessage().equals("A network error (such as timeout, interrupted connection or unreachable host) has occurred."))
                                    Toast.makeText(mContext, "Revisa tu conexión a internet",
                                        Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(mContext, "Algo salió mal :(",
                                            Toast.LENGTH_SHORT).show();
                                Log.e("NO LOGUEA",task.getException().getMessage());
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
    private boolean validateFields(){
        if(isStringNull(mEmail.getText().toString())){ //faltaria agregar validacion de mail existente
            displayMessage("El campo Email es obligatorio");
            return false;
        }
        if(isStringNull(mPassword.getText().toString())){
            displayMessage("Debes ingresar una contraseña");
            return false;
        }
        return true;
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
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(user.isEmailVerified()){
                        Intent intent = new Intent(mContext, HomeActivity.class);
                        Toast.makeText(mContext, "Bienvenido a myLook!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }else{
                        displayMessage("Tu email aún no esta verificado");
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                } else {
                }
            }
        };
    }

    private void initElements(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mLayout = (LinearLayout) findViewById(R.id.login_form);
        btnLogin = (Button) findViewById(R.id.login_button);
        signUpLink = (TextView) findViewById(R.id.link_signup);
    }
    private void getIncomingIntent(){
        Intent intent=getIntent();
        if(intent.hasExtra("email")){
            Log.d("IncomingIntent", "getIncomingIntent: found intent extras.");
            mEmail.setText(intent.getStringExtra("email"));
        }
    }
}

