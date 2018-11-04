package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class ClosetActivity extends AppCompatActivity {


    private Toolbar tb;
    private MenuItem filterMenuItem;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Favorite> favorites;
    private  FavouritesTab newFabTab;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet_tablayout);
        initElements();
        createTabLayout();

    }

    private void createTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.closetViewPager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ClosetTabAdapter adapter = new ClosetTabAdapter(getSupportFragmentManager(), 2);
        newFabTab = new FavouritesTab();
        adapter.addFragment(newFabTab, "Favoritos");
        adapter.addFragment(new CategoryTab(), "Colección");
        viewPager.setAdapter(adapter);
    }

    private void initElements() {
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Mi Ropero");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.closet_menu, menu);
        filterMenuItem = menu.findItem(R.id.new_outfit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_outfit) {
            Intent intent = new Intent(getApplication(), OutfitActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
