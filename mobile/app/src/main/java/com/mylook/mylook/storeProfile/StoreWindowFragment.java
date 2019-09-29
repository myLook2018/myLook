package com.mylook.mylook.storeProfile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.common.base.Strings;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class StoreWindowFragment extends Fragment {

    public StoreWindowFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopwindow, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        Bundle args = getArguments();

        if (args != null) {
            ImageView coverPh = rootView.findViewById(R.id.cover_store_photo);
            setCover(coverPh, args.getString("cover"));

            GridView grid = rootView.findViewById(R.id.gridview_store_window);
            fillGrid(grid, args.getString("storeName"));
        }
    }

    private void setCover(ImageView view, String cover) {
        if (!Strings.isNullOrEmpty(cover)) {
            Glide.with(view.getContext()).load(cover).into(view);
        }
    }

    private void fillGrid(final GridView grid, String storeName) {
        final ArrayList<Article> finalStoreWindowArticles = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereEqualTo("storeName", storeName)
                // TODO change name a isStoreFront
                .whereEqualTo("isStorefront", true)
                .get()
                .addOnSuccessListener(result -> {
                    for (DocumentSnapshot document : result.getDocuments()) {
                        try {
                            Article art = document.toObject(Article.class);
                            art.setArticleId(document.getId());
                            finalStoreWindowArticles.add(art);
                        } catch (Exception e) {
                            Log.d("FILL STORE WINDOW", String.format("Error al obtener el articulo con id %s para la vidriera de la tienda %s", document.getId(), storeName));
                        }
                    }
                    grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, finalStoreWindowArticles));
                })
                .addOnFailureListener(err -> Log.e("Firestore task", "onFailure: " + err));
    }
}


