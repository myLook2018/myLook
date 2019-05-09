package com.mylook.mylook.closet;

import android.content.DialogInterface;
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

public class ClosetFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static FavouritesTab newFabTab;
    private static OutfitsTab outfitsTab;
    public final static String TAG = "ClosetFragment";
    private static ClosetFragment homeInstance = null;
    private ClosetTabAdapter adapter;
    private static boolean loaded = false;

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
        if (!loaded) {
            adapter = new ClosetTabAdapter(getChildFragmentManager(), 2);
            newFabTab = FavouritesTab.getInstance();
            adapter.addFragment(newFabTab, "Tus prendas");
            outfitsTab = OutfitsTab.getInstance();
            adapter.addFragment(outfitsTab, "Conjuntos");
        }
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static void refreshStatus(){
        if(homeInstance!=null){
            loaded = false;
            outfitsTab.refreshStatus();
            newFabTab.refreshStatus();
        }
    }


}
