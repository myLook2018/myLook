package com.mylook.mylook.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.login.LoginActivity;

public class NewPasswordActivity extends AppCompatActivity {
    private EditText oldPassword, newPassword, newPasswordVerif;
    private TextInputLayout oldPassInput, newPassInput, newPassVerifInput;
    private Button btnChange;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar tb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initializeElements();

    }

    private void initializeElements() {
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        newPasswordVerif = findViewById(R.id.newPasswordVerification);
        btnChange = findViewById(R.id.btn_changePassword);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Cambiar contraseña");
        newPassInput = findViewById(R.id.newPasswordInput);
        newPassVerifInput = findViewById(R.id.newPasswordVerifInput);
        oldPassInput = findViewById(R.id.oldPasswordInput);
        newPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        newPassVerifInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        oldPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        oldPassInput.setErrorEnabled(false);
        newPassVerifInput.setErrorEnabled(false);
        newPassInput.setErrorEnabled(false);
    }

    private void validateFields() {
        oldPassInput.setErrorEnabled(false);
        newPassVerifInput.setErrorEnabled(false);
        newPassInput.setErrorEnabled(false);
        Log.e("New Password", "Validating fields");
        oldPassword.clearFocus();
        newPassword.clearFocus();
        newPasswordVerif.clearFocus();
        Log.e("Verify passwords", " " + newPassword.getText().toString().equals(newPasswordVerif.getText().toString()));
        if (newPassword.getText().toString().equals(newPasswordVerif.getText().toString())) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), oldPassword.getText().toString());
            Log.e("Credential", credential.toString());
            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {
                    user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DialogManager.getInstance().succesfulChangedPassword(NewPasswordActivity.this,
                                        "Cambiar Contraseña",
                                        "La contraseña se cambió correctamente, debe volver a iniciar sesión para aplicar el cambio",
                                        "Aceptar").show();
                                Log.e("New Password", "Password updated");
                            } else {
                                Log.e("New Password", "Error password not updated");
                            }
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewPasswordActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                    oldPassInput.setErrorEnabled(true);

                    oldPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
                    oldPassInput.setError("Contraseña incorrecta");
                }
            });
        } else {
            Log.e("New Password", "COntraseñas no coinciden");
            newPassInput.setError("Contraseñas diferentes");
            newPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
            newPassVerifInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
            newPassInput.setErrorEnabled(true);
            newPassVerifInput.setErrorEnabled(true);
        }
    }
}
