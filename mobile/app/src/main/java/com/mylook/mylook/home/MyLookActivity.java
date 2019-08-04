package com.mylook.mylook.home;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mylook.mylook.R;
import com.mylook.mylook.closet.ClosetFragment;
import com.mylook.mylook.explore.ExploreFragment;
import com.mylook.mylook.profile.ProfileFragment;
import com.mylook.mylook.recommend.RecommendFragment;
import com.mylook.mylook.session.Session;

public class MyLookActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ExploreFragment.OnNavbarToggle {
    Fragment fragment = null;
    boolean isPremium;
    BottomNavigationView navigation;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPremium= Session.getInstance().isPremiumUser();
        loadFragment(HomeFragment.getInstance());
        setContentView(R.layout.activity_mylook_app);
        navigation= findViewById(R.id.navigation);
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

    @Override
    public void onHide() {
        hideBottomNavigationView();
    }

    @Override
    public void onShow() {
        showBottomNavigationView();
    }

    // android:animateLayoutChanges="true" in constraint layout

    private void hideBottomNavigationView() {
        navigation.setVisibility(View.GONE);
    }

    public void showBottomNavigationView() {
        navigation.setVisibility(View.VISIBLE);
    }

}
