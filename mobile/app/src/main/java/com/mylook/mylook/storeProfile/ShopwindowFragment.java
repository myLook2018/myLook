package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
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

public class ShopwindowFragment extends Fragment {

    private FirebaseFirestore dB = null;
    private static String storeName;
    private static String coverPh;
    private GridImageAdapter adapter;

    public ShopwindowFragment() {
    }

    @SuppressLint("ValidFragment")
    public ShopwindowFragment(String name, String cover) {
        Log.d("Constructor Vidriera", "ShopwindowFragment: ENTRO");
        dB = FirebaseFirestore.getInstance();
        storeName = name;
        coverPh = cover;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("Window Fragment", "onCreateView: El container es " + container.toString());
        View rootView = inflater.inflate(R.layout.fragment_shopwindow, container, false);
        GridViewWithHeaderAndFooter gridWindow = rootView.findViewById(R.id.gridview_store_window);
        if (coverPh != null) {
            gridWindow.addHeaderView(createHeaderView());
        }
        setupShopWindowGridView(gridWindow);
        return rootView;
    }

    private View createHeaderView() {
        Log.d("Header Vidriera", "createHeaderView: Creo HEADER");
        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.grid_header, null, false);
        ImageView headerImage = view.findViewById(R.id.header);
        Glide.with(headerImage.getContext()).load(coverPh).into(headerImage);
        return view;
    }

    private void setupShopWindowGridView(final GridViewWithHeaderAndFooter grid) {
        Log.d("Store Window gridView", "setupGridView: Setting up store grid de la vidriera.");
        adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, getArticlesWindow());
        grid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.d("Grid Vidriera", "onComplete: Se setio el adapter del grid");
    }

    public ArrayList<Article> getArticlesWindow(){
        final ArrayList<Article> auxStoreWindowArticles = new ArrayList<>();
        dB.collection("articles").whereEqualTo("storeName", storeName)
                .whereEqualTo("estaEnVidriera", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentReference : task.getResult().getDocuments()) {
                        Article art = documentReference.toObject(Article.class);
                        art.setArticleId(documentReference.getId());
                        Log.d("Article Window", "id: " + art.getArticleId());
                        auxStoreWindowArticles.add(art);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("Aux Array Window ", "Size array: " + auxStoreWindowArticles.size());
                } else {
                    Log.e("Firestore task", "onComplete: " + task.getException());
                }
            }
        });
        return auxStoreWindowArticles;
    }
}


