package com.mylook.myapp.Window;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mylook.myapp.Utils.BottomNavigationViewHelper;
import com.mylook.myapp.Utils.ImageAdapter;
import com.mylook.myapp.R;

public class WindowActivity extends AppCompatActivity{
    private static final String TAG ="WindowActivity";
    private Context mcontext = WindowActivity.this;
    private static final int ACTIVITY_NUM = 1;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_test);
        Log.d(TAG, "onCreate: started.");

        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);

        setupBottomNavigationView();
        setupToolbar();

        GridView gridview = (GridView) findViewById(R.id.profileGridView);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(mcontext, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupToolbar(){

        /*Toolbar toolbar=(Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item: " + item);

                switch (item.getItemId()){
                    case R.id.profileMenu:
                        Log.d(TAG, "onMenuItemClick: Navigating to Profile Configuration.");
                }
                return false;
            }
        });*/

        ImageView profileMenu = (ImageView) findViewById(R.id.drop_down_option_menu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigating to account settings.");
                Intent intent = new Intent(mcontext,AccountSettingsActivity.class);
                startActivity(intent);

            }
        });


    }

    /* BottomNavigationView setup */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView in Window");
        BottomNavigationViewEx BottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(BottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mcontext, BottomNavigationViewEx);
        Menu menu=BottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }*/
}
