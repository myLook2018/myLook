package com.mylook.mylook.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mylook.mylook.R;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;

public class MyLookActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFragment(new HomeFragment(), "myLook");
        setContentView(R.layout.activity_mylook_app);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(MyLookActivity.this);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTheme(R.style.AppTheme);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_house:
                return loadFragment(new HomeFragment(), "myLook");
            case R.id.ic_explore:
                return loadFragment(new ExploreFragment(), "Explorar");
            case R.id.ic_recommend:
                return loadFragment(new RecommendFragment(), "Recomendaciones");
            case R.id.ic_closet:
                return loadFragment(new ClosetFragment(), "Ropero");
            case R.id.ic_profile:
                return loadFragment(new ProfileFragment(), "Perfil");
            default:
                return false;
        }
    }

    private boolean loadFragment(Fragment fragment, String title) {
        if (toolbar == null) return false;
        toolbar.setTitle(title);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }
}
