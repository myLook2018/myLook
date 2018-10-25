package com.mylook.mylook.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.mylook.mylook.R;

public class MyAccountActivity extends AppCompatActivity{
    private LinearLayout lnlInfoAccount;
    private LinearLayout lnlAdress;
    private LinearLayout lnlNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initElements();
        setOnClickListener();

        // setupBottomNavigationView();
    }

    private void setOnClickListener() {
        lnlInfoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), EditInfoActivity.class);
                startActivity(intent);
            }

        });
        lnlAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
        lnlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });
    }

    private void initElements() {
        lnlInfoAccount=findViewById(R.id.layoutInfoAccount);
        lnlAdress=findViewById(R.id.layouAdress);
        lnlNotifications=findViewById(R.id.layoutNotifications);
    }
}
