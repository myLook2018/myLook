package com.mylook.mylook.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mylook.mylook.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private AutoCompleteTextView mail;
    private Button recover;
    private Context mContext;
    private ProgressBar mProgressBar;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        initElements();
        mProgressBar.setVisibility(View.GONE);
        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                reset();
            }
        });
    }

    private void reset() {
        if (!mail.getText().equals("")) {
            if (isValidEmail(mail.getText().toString())) {
                Log.e("Mail","Valido. Por mandar reset email");
                final String emailAddress = mail.getText().toString();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.e("Task","COmpleta"+task.isComplete()+", Succesful "+task.isSuccessful());
                                if (task.isSuccessful()) {
                                    mProgressBar.setVisibility(View.GONE);
                                    displayMessage("Te mandamos un mail para que recuperes tu contraseña");
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    intent.putExtra("mail", emailAddress);
                                    startActivity(intent);
                                    finish();
                                } else{
                                    mProgressBar.setVisibility(View.GONE);
                                    displayMessage("El mail no se encuentra registrado");
                                    displayMessage("Todavía...");
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    intent.putExtra("mail", emailAddress);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } else {
                mProgressBar.setVisibility(View.GONE);
                displayMessage("Ese no es un mail válido");
            }
        } else {
            mProgressBar.setVisibility(View.GONE);
            displayMessage("¿Pusiste algún mail?");
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void initElements() {
        mContext = ResetPasswordActivity.this;
        mail = findViewById(R.id.recover_mail);
        recover = findViewById(R.id.recover_button);
        mProgressBar = findViewById(R.id.reset_progressBar);
    }
}
