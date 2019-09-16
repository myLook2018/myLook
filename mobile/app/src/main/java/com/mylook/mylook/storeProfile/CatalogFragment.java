package com.mylook.mylook.storeProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class CatalogFragment extends Fragment {

    public CatalogFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_catalog, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Bundle args = getArguments();

        if (args != null) {
            GridView grid = rootView.findViewById(R.id.gridview);
            fillGrid(grid, args.getString("name"));
        }
    }

    private void fillGrid(final GridView grid, String storeName) {
        final ArrayList<Article> storeArticles = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereEqualTo("storeName", storeName)
                .get()
                .addOnSuccessListener(result -> {
                    for (DocumentSnapshot document : result.getDocuments()) {
                        Article art = document.toObject(Article.class);
                        art.setArticleId(document.getId());
                        storeArticles.add(art);
                    }
                    Log.d("CATALOG GRID", String.format("La lista de articulos de la tienda con nombre %s tiene un size de ", storeName, storeArticles.size()));
                    grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, storeArticles));
                })
                .addOnFailureListener(err -> Log.e("Firestore task", "onFailure: " + err));
    }

}
