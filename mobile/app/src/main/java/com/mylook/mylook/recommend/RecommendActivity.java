package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mylook.mylook.R;
import com.mylook.mylook.utils.BottomNavigationViewHelper;
import com.mylook.mylook.utils.SectionsPagerAdapter;

public class RecommendActivity extends AppCompatActivity implements RecommendFragment.OnFragmentInteractionListener {

    private static final int ACTIVITY_NUM = 2;
    private FloatingActionButton fab;

    private Context mContext = RecommendActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        setupBottomNavigationView();
        fab= findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RecommendActivityAddDesc.class);
                startActivity(intent);
            }
        });
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Recomendaciones");
        setSupportActionBar(tb);
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
