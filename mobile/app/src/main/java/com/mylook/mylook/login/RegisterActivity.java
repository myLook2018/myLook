package com.mylook.mylook.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName,txtSurname,txtBirthdate;
    private TextView mWaiting;
    private LinearLayout mLayout;
    private Toolbar tb;
    private MaterialBetterSpinner spinner;
    private Button btnRegister;
    private FirebaseUser user;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        mProgressBar.setVisibility(View.GONE);
        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Registro");
        setSupportActionBar(tb);
        setupFirebaseAuth();
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private boolean validateFields(){
        if(isStringNull(txtEmail.getText().toString())){ //faltaria agregar validacion de mail existente
            displayMessage("El campo Email es obligatorio");
            return false;
        }
        if(isStringNull(txtPasswd1.getText().toString())){
            displayMessage("Debes ingresar una contraseña");
            return false;
        }
        if(txtPasswd1.getText().length()<6){
            displayMessage("La contraseña debe contener 6 caracteres");
            txtPasswd1.setText("");
            txtPasswd2.setText("");
            return false;
        }
        if(isStringNull(txtPasswd2.getText().toString())){
            displayMessage("Debes ingresar ambas contraseñas");
            return false;
        }
        if(!txtPasswd1.getText().toString().equals(txtPasswd2.getText().toString())){
            displayMessage("Las contraseñas no coinciden");
            return false;
        }
        if(isStringNull(txtName.getText().toString())){
            displayMessage("El campo Nombre es obligatorio");
            return false;
        }
        if(isStringNull(txtSurname.getText().toString())){
            displayMessage("El campo Apellido es obligatorio");
            return false;
        }
        if(isStringNull(txtBirthdate.getText().toString())){
            displayMessage("Debes ingresar una Fecha de Nacimiento");
            return false;
        }
       /* if(spinner.getSelectedItemPosition()==0){
            displayMessage("El campo Sexo es obligatorio");
            return false;
        }*/
        return true;
    }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void register(){
        if(validateFields()){
            mProgressBar.setVisibility(View.VISIBLE);
            mLayout.setVisibility(View.GONE);
            String email=txtEmail.getText().toString();
            String passwd=txtPasswd1.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                                logInIntent();
                                //setupFirebaseAuth();
                            } else {
                                Log.w("Register","createUserWithEmail:Failure",task.getException());
                                Toast.makeText(RegisterActivity.this, "Algo salio mal",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
    private void logInIntent(){
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("email", txtEmail.getText());
        startActivity(intent);
        finish();
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                }
            }
        };
    }
    private void initElements(){
        btnRegister = (Button) findViewById(R.id.register_button);
        mProgressBar = (ProgressBar) findViewById(R.id.register_progressbar);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPasswd1 = (EditText) findViewById(R.id.txtPasswd);
        txtPasswd2= (EditText)findViewById(R.id.txtPasswd2);
        txtName= (EditText)findViewById(R.id.txtName);
        txtSurname= (EditText)findViewById(R.id.txtSurname);
        txtBirthdate= (EditText)findViewById(R.id.txtBirthdate);
        mLayout = (LinearLayout) findViewById(R.id.register_form);
        mContext = RegisterActivity.this;
        spinner = (MaterialBetterSpinner) findViewById(R.id.spinner);
        setSpinnerSex();
    }
    private void setSpinnerSex(){
        String[] sexoType = {"Femenino","Masculino","Otre"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexoType));
    }
    private void sendEmailVerification() {
         // Send verification email
        // [START send_email_verification]
        user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Verifica tu mail y luego inicia sesion ",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("SendEmailVerification", "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }
}
