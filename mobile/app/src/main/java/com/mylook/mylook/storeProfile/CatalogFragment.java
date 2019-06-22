package com.mylook.mylook.storeProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class CatalogFragment extends Fragment {

    public CatalogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridViewWithHeaderAndFooter grid = view.findViewById(R.id.gridview);
        setupGridView(grid);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupGridView(final GridViewWithHeaderAndFooter grid) {

        Log.d("Store gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeArticles = new ArrayList<Article>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereEqualTo("storeName", getArguments().getString("name")).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot documentReference : task.getResult().getDocuments()) {
                            Article art = documentReference.toObject(Article.class);
                            art.setArticleId(documentReference.getId());
                            storeArticles.add(art);
                        }
                        grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, storeArticles));
                    }
                });

    }
}
