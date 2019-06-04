package com.mylook.mylook.home;

import android.annotation.SuppressLint;
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
import com.mylook.mylook.session.Sesion;

public class MyLookActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private String activeFragment;
    Fragment fragment = null;
    private Sesion currentSesion;
    boolean isPremium;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeFragment = HomeFragment.TAG;
        isPremium= Sesion.getInstance().isPremiumUser();
        loadFragment(HomeFragment.getInstance());
        setContentView(R.layout.activity_mylook_app);
        BottomNavigationView navigation= findViewById(R.id.navigation);
        if(isPremium)
            navigation.inflateMenu(R.menu.bottom_navigation_menu_premium);
        else
            navigation.inflateMenu(R.menu.bottom_navigation_menu);

        navigation.setOnNavigationItemSelectedListener(MyLookActivity.this);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setTheme(R.style.AppTheme);
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
