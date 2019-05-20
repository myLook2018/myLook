package com.mylook.mylook.storeProfile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class ShopwindowFragment extends Fragment {

    public ShopwindowFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopwindow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridViewWithHeaderAndFooter grid = view.findViewById(R.id.gridview);
        if (getArguments().getString("cover") != null) grid.addHeaderView(createHeaderView());
        setupShopWindowGridView(grid);
        super.onViewCreated(view, savedInstanceState);
    }

    private View createHeaderView() {
        //TODO esto da asco
        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.grid_header, null, false);
        ImageView image = view.findViewById(R.id.header);
        Glide.with(image.getContext()).load(getArguments().getString("cover")).into(image);
        return view;
    }

    private void setupShopWindowGridView(final GridViewWithHeaderAndFooter grid) {
        //TODO esto da asco
        Log.d("Store Catalog gridView", "setupGridView: Setting up store grid.");
        final ArrayList<Article> storeShopWindowArticles = new ArrayList<Article>();
        final String[] documentID = new String[1];
        FirebaseFirestore.getInstance().collection("storeFronts")
                .whereEqualTo("storeName", getArguments().getString("name")).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        documentID[0] = task.getResult().getDocuments().get(0).getId();
                        FirebaseFirestore.getInstance().collection("storeFronts")
                                .document(documentID[0]).collection("storeFronts").get()
                                .addOnCompleteListener(task1 -> {
                                    ArrayList<HashMap> array = (ArrayList<HashMap>) task1.getResult().getDocuments().get(0).get("articles");
                                    for (HashMap o : array) {
                                        Article art = new Article();
                                        storeShopWindowArticles.add(art.toObject(o));
                                    }
                                });
                        grid.setAdapter(new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, storeShopWindowArticles));
                    }
                });

    }

}


