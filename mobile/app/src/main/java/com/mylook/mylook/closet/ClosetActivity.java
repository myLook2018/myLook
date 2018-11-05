package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mylook.mylook.R;

public class ClosetActivity extends AppCompatActivity {


    private Toolbar tb;
    private MenuItem filterMenuItem;
    private TabLayout tabLayout;
    private ViewPager viewPager;
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
        ClosetTabAdapter adapter = new ClosetTabAdapter(getSupportFragmentManager(),2);
        adapter.addFragment(new  FavouritesTab(), "Favoritos");
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
        Log.e("Crete Options Menu", "Finalmente entro");
        getMenuInflater().inflate(R.menu.closet_menu, menu);
        filterMenuItem = menu.findItem(R.id.new_outfit);
        Log.e("Options Menu", filterMenuItem.getTitle().toString());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_outfit) {
            Intent intent = new Intent(getApplicationContext(), OutfitActivity.class);
            //intent.putExtra("favoritos", favorites);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
