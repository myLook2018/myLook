package com.mylook.mylook.profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class EditInfoActivity extends AppCompatActivity {

    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName,txtSurname,txtBirthdate;
    private LinearLayout mLayout;
    private MaterialBetterSpinner spinner;
    private Button btnSaveChanges;
    private FirebaseUser user;
    private FirebaseFirestore dB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        setOnClickListener();

        // setupBottomNavigationView();
    }

    private void setOnClickListener() {
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //graba en base de datos
            }
        });
    }

    private void initElements() {
        txtEmail=findViewById(R.id.txtEmail);
        txtPasswd1=findViewById(R.id.txtPasswd);
        txtPasswd2=findViewById(R.id.txtPasswd2);
        txtDNI=findViewById(R.id.txtDNI);
        txtName=findViewById(R.id.txtName);
        txtSurname=findViewById(R.id.txtSurname);
        txtBirthdate=findViewById(R.id.txtBirthdate);
        btnSaveChanges=findViewById(R.id.btnRegister);
        btnSaveChanges.setText("Aplicar");
    }
    private void setData(){
        //grabar datos por defecto
    }


}
