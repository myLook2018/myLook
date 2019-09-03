package com.mylook.mylook.session;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.home.MyLookActivity;
import com.mylook.mylook.login.LoginActivity;
import com.mylook.mylook.login.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.getInstance() != null) {
            Task<QuerySnapshot> task = Session.getInstance().initializeElements();
            if (task != null) {
                task.addOnSuccessListener(task1 -> {
                    if(task.getResult().getDocuments().size() > 0) {
                        Intent intent = new Intent(MainActivity.this, MyLookActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    intent.putExtra("mail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    intent.putExtra("displayName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    intent.putExtra("provider", "google");
                    }
                });
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                try {
                    finish();
                } catch (Throwable e) {
                    Log.e("Current session is null: couldn't finish MainActivity", e.getMessage());
                }
            }
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            try {
                finish();
            } catch (Throwable e) {
                Log.e("Current session is null: couldn't finish MainActivity", e.getMessage());
            }
        }
    }
}