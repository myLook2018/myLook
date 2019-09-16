package com.mylook.mylook.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mylook.mylook.R;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;
import com.mylook.mylook.session.Session;

public class MyLookActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    private Session currentSesion;
    private static final String TAG = "MyLookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFragment(HomeFragment.getInstance());
        setContentView(R.layout.activity_mylook_app);
        BottomNavigationView navigation= findViewById(R.id.navigation);
        if(Session.getInstance().isPremiumUser())
            navigation.inflateMenu(R.menu.bottom_navigation_menu_premium);
        else
            navigation.inflateMenu(R.menu.bottom_navigation_menu);

        navigation.setOnNavigationItemSelectedListener(MyLookActivity.this);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTheme(R.style.AppTheme);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFragment(HomeFragment.getInstance());
        setContentView(R.layout.activity_mylook_app);
        BottomNavigationView navigation= findViewById(R.id.navigation);
        if(Session.getInstance().isPremiumUser())
            navigation.inflateMenu(R.menu.bottom_navigation_menu_premium);
        else
            navigation.inflateMenu(R.menu.bottom_navigation_menu);

        navigation.setOnNavigationItemSelectedListener(MyLookActivity.this);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTheme(R.style.AppTheme);
    }
    public void setPremiumMenu(){
        BottomNavigationView navigation= findViewById(R.id.navigation);
        navigation.inflateMenu(R.menu.bottom_navigation_menu);
    }
    @SuppressLint("NewApi")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_house:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("myLook");
                fragment = HomeFragment.getInstance();
                break;
            case R.id.ic_explore:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Explorar");
                fragment = ExploreFragment.getInstance();
                break;
            case R.id.ic_recommend:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Recomendaciones");
                fragment = RecommendFragment.getInstance();
                break;
            case R.id.ic_closet:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Ropero");
                fragment = ClosetFragment.getInstance();
                break;
            case R.id.ic_profile:
                ((Toolbar) findViewById(R.id.main_toolbar)).setTitle("Perfil");
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