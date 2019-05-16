package com.mylook.mylook.closet;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;

public class ClosetFragment extends Fragment {

    private TabLayout tabLayout;
    private FavoritesViewModel favModel;

    public ClosetFragment() { }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createTabLayout(view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        FavoritesViewModel model = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        model.getFavorites().observe(this, favorites -> {
            // update UI
        });
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_closet_tablayout, null);
    }

    private void createTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tablayout);
        ViewPager viewPager = view.findViewById(R.id.closetViewPager);
        setupViewPager(viewPager);
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
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        final android.app.AlertDialog alert = dialogBuilder.setTitle("Ayuda")
                .setPositiveButton("Aceptar", (dialog, which) ->
                        dialog.cancel()).setMessage("Acá podés ver toda tu ropa favorita y los conjuntos que armás. Para armar un " +
                        "nuevo conjunto andá a la pestaña conjuntos y apretá en el botón +")
                .create();
        alert.setOnShowListener(dialog1 -> alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE));
        alert.show();
    }

    private void setupViewPager(ViewPager viewPager) {
        ClosetTabAdapter adapter = new ClosetTabAdapter(getChildFragmentManager(), 2);
        FavouritesTab newFabTab = new FavouritesTab();
        adapter.addFragment(newFabTab, "Tus prendas");
        OutfitsTab outfitsTab = new OutfitsTab();
        adapter.addFragment(outfitsTab, "Conjuntos");
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
    }

}
