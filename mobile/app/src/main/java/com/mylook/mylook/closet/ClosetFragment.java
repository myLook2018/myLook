package com.mylook.mylook.closet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.recommend.RecommendActivityAddDesc;

import java.util.ArrayList;

public class ClosetFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FavouritesTab newFabTab;
    private CategoryTab categoryTab;
    public final static String TAG = "ClosetFragment";
    private static ClosetFragment homeInstance = null;
    private ClosetTabAdapter adapter;
    private boolean loaded = false;

    public static ClosetFragment getInstance() {
        if (homeInstance == null) {
            homeInstance = new ClosetFragment();
        }
        return homeInstance;
    }


    public ClosetFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTabLayout(view);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Recomendaciones");
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_closet_tablayout, null);
    }


    private void createTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.closetViewPager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.closet_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.help_menu) {
            createHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createHelpDialog() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        final android.app.AlertDialog alert = dialog.setTitle("Ayuda")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setMessage("Acá podés ver toda tu ropa favorita y los conjuntos que armás. Para armar un " +
                        "nuevo conjunto andá a la pestaña conjuntos y apretá en el botón +")
                .create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
            }
        });

        alert.show();
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ClosetTabAdapter(getChildFragmentManager(), 2);
        newFabTab = FavouritesTab.getInstance();
        adapter.addFragment(newFabTab, "Tus prendas");
        categoryTab = CategoryTab.getInstance();
        adapter.addFragment(categoryTab, "Conjuntos");
        viewPager.setAdapter(adapter);
        loaded = true;
    }


}
