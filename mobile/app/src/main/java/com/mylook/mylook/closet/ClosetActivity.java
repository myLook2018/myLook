package com.mylook.mylook.closet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.mylook.mylook.R;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.utils.BottomNavigationViewHelper;

import java.util.ArrayList;


public class ClosetActivity extends AppCompatActivity {



    private static final int ACTIVITY_NUM = 3;
    private Toolbar tb;
    private MenuItem filterMenuItem;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Favorite> favorites;
    private FavouritesTab newFabTab;
    private BottomNavigationView bottomNavigationView;

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
        adapter.addFragment(newFabTab, "Tus prendas");
        adapter.addFragment(new CategoryTab(), "Conjuntos");
        viewPager.setAdapter(adapter);
    }

    private void initElements() {
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Mi Ropero");
        setSupportActionBar(tb);
        setupBottomNavigationView();
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
            createInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void createInputDialog() {

        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ClosetActivity.this, R.style.AlertDialogTheme);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        input.setMaxWidth(100);
        input.setHint((CharSequence) "Nombre");
        dialog.setView(input);

        final android.app.AlertDialog alert = dialog.setTitle("Elegí un nombre para tu conjunto")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        String newOutfitName = input.getText().toString();
                        Intent intent = new Intent(getApplication(), OutfitActivity.class);
                        intent.putExtra("name", newOutfitName);
                        intent.putExtra("category", "normal");
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                    }


                }).create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple));
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
            }
        });

        alert.show();
    }
    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
         bottomNavigationView =  findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(ClosetActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}

