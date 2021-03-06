package com.mylook.mylook.profile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class NewPasswordActivity extends AppCompatActivity {
    private EditText oldPassword, newPassword, newPasswordVerif;
    private TextInputLayout oldPassInput, newPassInput, newPassVerifInput;
    private ProgressBar mProgressBar;
    private ImageButton btnChange;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar tb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initializeElements();

    }

    private void initializeElements() {
        oldPassword = findViewById(R.id.txtOldPassword);
        newPassword = findViewById(R.id.txtPassword);
        newPasswordVerif = findViewById(R.id.txtPasswordVerif);
        btnChange = findViewById(R.id.btn_changePassword);
        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        tb = findViewById(R.id.info_account_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Cambiar Contraseña");
        newPassInput = findViewById(R.id.newPasswordInput);
        newPassVerifInput = findViewById(R.id.newPasswordVerifInput);
        oldPassInput = findViewById(R.id.oldPasswordInput);
        newPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        newPassVerifInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        oldPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
        oldPassInput.setErrorEnabled(false);
        newPassVerifInput.setErrorEnabled(false);
        newPassInput.setErrorEnabled(false);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

    }

    private void validateFields() {
        mProgressBar.setVisibility(View.VISIBLE);
        oldPassInput.setErrorEnabled(false);
        newPassVerifInput.setErrorEnabled(false);
        newPassInput.setErrorEnabled(false);
        Log.e("New Password", "Validating fields");
        oldPassword.clearFocus();
        newPassword.clearFocus();
        newPasswordVerif.clearFocus();
        if (newPassword.getText().length() < 6 || newPasswordVerif.getText().length() < 6) {
            Toast.makeText(NewPasswordActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT);
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
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
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    DialogManager.getInstance().succesfulChangedPassword(NewPasswordActivity.this,
                                            "Cambiar Contraseña",
                                            "La contraseña se cambió correctamente, debe volver a iniciar sesión para aplicar el cambio",
                                            "Aceptar").show();
                                    Log.e("New Password", "Password updated");
                                } else {
                                    Log.e("New Password", "Error password not updated");
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(NewPasswordActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                        oldPassInput.setErrorEnabled(true);

                        oldPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
                        oldPassInput.setError("Contraseña incorrecta");
                    }
                });
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.e("New Password", "COntraseñas no coinciden");
                newPassInput.setError("Contraseñas diferentes");
                newPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
                newPassVerifInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
                newPassInput.setErrorEnabled(true);
                newPassVerifInput.setErrorEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
