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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

public class ShopwindowFragment extends Fragment {

    public ShopwindowFragment() {
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
            GridView grid = rootView.findViewById(R.id.gridview);
            fillGrid(grid, args.getString("name"));
        }
    }

    private void setCover(ImageView view, String cover) {
        if (cover != null && !cover.equals("")) {
            Glide.with(view.getContext()).load(cover).into(view);
        }
    }

    private void fillGrid(final GridView grid, String storeName) {
        final ArrayList<Article> storeShopWindowArticles = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereEqualTo("storeName", storeName)
                // TODO change name
                .whereEqualTo("isStorefront", true)
                .get()
                .addOnSuccessListener(result -> {
                    for (DocumentSnapshot document : result.getDocuments()) {
                        Article art = document.toObject(Article.class);
                        art.setArticleId(document.getId());
                        storeShopWindowArticles.add(art);
                        Log.e("VIDRIERA", "Encontro articulo");
                    }
                    grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.ripple_image_view, storeShopWindowArticles));
                })
                .addOnFailureListener(err -> Log.e("Firestore task", "onFailure: " + err));
    }

}


