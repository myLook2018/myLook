package com.mylook.mylook.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        ImageButton btnChange = findViewById(R.id.btn_changePassword);
        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        Toolbar tb = findViewById(R.id.info_account_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
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
        btnChange.setOnClickListener(v -> validateFields());
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
            Toast.makeText(NewPasswordActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.INVISIBLE);
        } else {
            if (newPassword.getText().toString().equals(newPasswordVerif.getText().toString())) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPassword.getText().toString());
                Log.e("Credential", credential.toString());
                user.reauthenticate(credential).addOnSuccessListener(aVoid ->
                        user.updatePassword(newPassword.getText().toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        new DialogManager().succesfulChangedPassword(NewPasswordActivity.this,
                                                "Cambiar Contraseña",
                                                "La contraseña se cambió correctamente, debe volver a iniciar sesión para aplicar el cambio",
                                                "Aceptar").show();
                                        Log.e("New Password", "Password updated");
                                    } else {
                                        Log.e("New Password", "Error password not updated");
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                })).addOnFailureListener(e -> {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(NewPasswordActivity.this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                                    oldPassInput.setErrorEnabled(true);
                                    oldPassInput.setErrorTextColor(getResources().getColorStateList(R.color.red));
                                    oldPassInput.setError("Contraseña incorrecta");
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
        finish();
    }
}
