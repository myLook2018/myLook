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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.home.HomeActivity;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName,txtSurname,txtBirthdate;
    private LinearLayout mLayout;
    private Toolbar tb;
    private MaterialBetterSpinner spinner;
    private Button btnRegister;
    private FirebaseUser user;
    private FirebaseFirestore dB;



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
        //setupFirebaseAuth();
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
                                Log.d("REGISTER", "Esta pro guardar datos");

                                boolean saved=saveClient();
                                //setupFirebaseAuth();
                            } else {
                                Log.w("REGISTER","createUserWithEmail:Failure",task.getException());
                                Toast.makeText(RegisterActivity.this, "Algo salio mal",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void createCloset(){
        Closet closet=new Closet(mAuth.getUid());
        final String[] closetId = new String[1];
        dB.collection("closets").add(closet)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Log.e("CLOSET ON COMPLET", task.getResult().getId());
            }
        });
    }

    private boolean saveClient(){
        final boolean[] saved = new boolean[1];
        createCloset();
        final Map<String,Object> client=new HashMap<>();
        client.put("email",txtEmail.getText().toString());
        client.put("dni",txtDNI.getText().toString());
        client.put("name",txtName.getText().toString());
        client.put("surname",txtSurname.getText().toString());
        client.put("birthday",txtBirthdate.getText().toString());
        client.put("gender",spinner.getText().toString());
        client.put("userId",mAuth.getUid());
        client.put("isPremium",false);
        dB.collection("clients")
                .add(client).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("SAVE_CLIENT", "Se guarda client");
                saved[0] =true;
                sendEmailVerification();
                logInIntent();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SAVE_Client", "No se guarda cliente ", e.getCause());

                saved[0] =false;
                Toast.makeText(RegisterActivity.this, "Algo salio mal",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return saved[0];
    }
    @Override
    protected void onStart() {
        super.onStart();
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
        intent.putExtra("email", txtEmail.getText().toString());
        startActivity(intent);
        finish();
    }

    private void setupFirebaseAuth() {

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
        dB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnRegister = (Button) findViewById(R.id.btnRegister);
        mProgressBar = (ProgressBar) findViewById(R.id.register_progressbar);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPasswd1 = (EditText) findViewById(R.id.txtPasswd);
        txtPasswd2= (EditText)findViewById(R.id.txtPasswd2);
        txtDNI=(EditText)findViewById(R.id.txtDNI);
        txtName= (EditText)findViewById(R.id.txtName);
        txtSurname= (EditText)findViewById(R.id.txtSurname);
        txtBirthdate= (EditText)findViewById(R.id.txtBirthdate);
        mLayout = (LinearLayout) findViewById(R.id.register_form);
        mContext = RegisterActivity.this;
        spinner = (MaterialBetterSpinner) findViewById(R.id.spinner);
        setCategoryRequest();
    }

    private void setCategoryRequest() {
        dB.collection("categories").whereEqualTo("name", "sexo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> categories = (ArrayList<String>) task.getResult().getDocuments().get(0).get("categories");
                spinner.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_selectable_list_item, categories));
            }
        });
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
                            Log.d("EMAIL_VERIFICATION", "Se verifico mail");
                            Toast.makeText(RegisterActivity.this,
                                    "Verifica tu mail y luego inicia sesion ",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("EMAIL_VERIFICATION", "NO se verifico mail",task.getException());
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
