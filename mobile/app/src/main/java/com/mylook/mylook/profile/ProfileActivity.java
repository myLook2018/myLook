package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylook.mylook.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 3;
    private LinearLayout layoutAccount;
    private LinearLayout layoutCloset;
    private LinearLayout layoutSettings;
    private LinearLayout layoutHelp;
    private LinearLayout layoutExit;
    private TextView txtName;
    private TextView txtEmail;

    private Context mContext = ProfileActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initElements();
        setOnClickListener();

       // setupBottomNavigationView();
    }

    private void initElements(){
        layoutAccount=findViewById(R.id.layoutAccount);
        layoutCloset=findViewById(R.id.layoutCloset);
        layoutSettings=findViewById(R.id.layoutSettings);
        layoutHelp=findViewById(R.id.layoutHelp);
        layoutExit=findViewById(R.id.layoutExit);
        txtEmail=findViewById(R.id.txtEmail);
        txtName=findViewById(R.id.txtName);
    }
    private void setOnClickListener(){
        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });
        layoutCloset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
       // BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
