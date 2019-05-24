package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

import static android.app.Activity.RESULT_OK;

public class ShopwindowFragment extends Fragment {

    private FirebaseFirestore dB = null;
    private static String storeName;
    private static String coverPh;
    private GridImageAdapter adapter;

    public ShopwindowFragment() {
    }

    @SuppressLint("ValidFragment")
    public ShopwindowFragment(String name, String cover) {
        dB = FirebaseFirestore.getInstance();
        storeName = name;
        coverPh = cover;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Window Fragment", "onCreateView: El container es " + container.toString());
        View rootView = inflater.inflate(R.layout.fragment_shopwindow, container, false);
        // Obtención del grid view
        GridViewWithHeaderAndFooter gridWindow = rootView.findViewById(R.id.gridview_store_window);
        // Inicializar el grid view
        if (coverPh != null){
            gridWindow.addHeaderView(createHeaderView());
        }
        setupShopWindowGridView(gridWindow);
        return rootView;
    }

    private View createHeaderView() {

        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.grid_header, null, false);
        // Seteando Imagen
        ImageView headerImage = view.findViewById(R.id.header);
        Glide.with(headerImage.getContext()).load(coverPh).into(headerImage);
        return view;
    }

    private void setupShopWindowGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store Window gridView", "setupGridView: Setting up store grid de la vidriera.");
        final ArrayList<Article> storeShopWindowArticles = new ArrayList<>();
        final String[] documentID = new String[1];
        dB.collection("storeFronts").whereEqualTo("storeName", storeName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            documentID[0] = task.getResult().getDocuments().get(0).getId();
                            Log.d("get store front", "onComplete: " + documentID[0]);
                            dB.collection("storeFronts").document(documentID[0]).collection("storeFronts").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            ArrayList<HashMap> array = (ArrayList<HashMap>) task.getResult().getDocuments().get(0).get("articles");
                                            for (HashMap o : array) {
                                                Article art = new Article();
                                                storeShopWindowArticles.add(art.toObject(o));
                                            }
                                        }
                                    });
                            Log.e("VIDRIERAAAA", getActivity().getLocalClassName());
                            adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, storeShopWindowArticles);
                            grid.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            if (task.getException() != null)
                                Log.e("Firestore task", "onComplete: " + task.getException());
                            else
                                Log.e("Firestore task", "onComplete: No existe vidriera");

                        }
                    }
                });

    }

}


