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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class CatalogFragment extends Fragment {

    private FirebaseFirestore dB = null;
    private static String storeName;

    public CatalogFragment() {
    }

    @SuppressLint("ValidFragment")
    public CatalogFragment(String name) {
        dB = FirebaseFirestore.getInstance();
        storeName = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Catalog Fragment", "onCreateView: El container es " + container.toString());
        View rootView = inflater.inflate(R.layout.fragment_store_catalog, container, false);
        // Obtención del grid view
        GridViewWithHeaderAndFooter gridCatalogo = rootView.findViewById(R.id.gridview_store_catalog);
        // Inicializar el grid view
        setupGridView(gridCatalogo);
        return rootView;
    }


    private void setupGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store Catalogo gridView", "setupGridView: Setting up store grid del catalogo.");
        final ArrayList<Article> storeArticles = new ArrayList<Article>();
        dB.collection("articles").whereEqualTo("storeName", storeName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentReference : task.getResult().getDocuments()) {
                        Article art = documentReference.toObject(Article.class);
                        art.setArticleId(documentReference.getId());
                        storeArticles.add(art);
                    }
                    Log.e("CATALOGOOOOO", getActivity().getLocalClassName());
                    grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, storeArticles));
                } else {
                    Log.e("Firestore task", "onComplete: " + task.getException());
                }
            }
        });

    }
}
