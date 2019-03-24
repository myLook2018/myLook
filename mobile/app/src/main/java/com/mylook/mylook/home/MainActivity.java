package com.mylook.mylook.home;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.mylook.mylook.R;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private String activeFragment;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        toolbar =findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        activeFragment = HomeFragment.TAG;
        loadFragment(HomeFragment.getInstance());
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_house:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("myLook");
                activeFragment = HomeFragment.TAG;
                fragment = HomeFragment.getInstance();
                break;
            case R.id.ic_explore:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Explorar");
                activeFragment = ExploreFragment.TAG;
                fragment = ExploreFragment.getInstance();
                break;
            case R.id.ic_recommend:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Recomendaciones");
                activeFragment = RecommendFragment.TAG;
                fragment = RecommendFragment.getInstance();
                break;
            case R.id.ic_closet:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Ropero");
                activeFragment = ClosetFragment.TAG;
                fragment = ClosetFragment.getInstance();
                break;
            case R.id.ic_profile:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Perfil");
                activeFragment = ProfileFragment.TAG;
                fragment = ProfileFragment.getInstance();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private boolean loadFragment(Fragment frag) {
        if (frag != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .commit();
            return true;
        }
        return false;
    }
}
