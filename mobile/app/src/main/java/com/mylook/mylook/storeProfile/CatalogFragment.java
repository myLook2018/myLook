package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private GridImageAdapter adapter;

    public CatalogFragment() {
    }

    @SuppressLint("ValidFragment")
    public CatalogFragment(String name) {
        Log.d("Constructor CATALOGO", "CatalogFragment: ENTRO");
        dB = FirebaseFirestore.getInstance();
        storeName = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Catalog Fragment", "onCreateView: El container es " + container.toString());
        View rootView = inflater.inflate(R.layout.fragment_store_catalog, container, false);
        GridViewWithHeaderAndFooter gridCatalogo = rootView.findViewById(R.id.gridview_store_catalog);
        setupGridView(gridCatalogo);
        return rootView;
    }


    private void setupGridView(final GridViewWithHeaderAndFooter grid) {
        Log.d("Store Catalogo gridView", "setupGridView: Setting up store grid del catalogo.");
        adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, getCatalogStore());
        grid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Article> getCatalogStore() {
        final ArrayList<Article> auxCatalogArticles = new ArrayList<>();
        dB.collection("articles").whereEqualTo("storeName", storeName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentReference : task.getResult().getDocuments()) {
                        Article art = documentReference.toObject(Article.class);
                        art.setArticleId(documentReference.getId());
                        auxCatalogArticles.add(art);
                    }
                    Log.d("Aux Array Catalog ", "Size array: " + auxCatalogArticles.size());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Firestore task", "onComplete: " + task.getException());
                }
            }
        });
        return auxCatalogArticles;
    }
}
