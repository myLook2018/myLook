package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.utils.ImageAdapter;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class FavouritesTab extends Fragment {

    private static final int FAVOURITE_INFO = 1;
    private GridView gridview;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Article> favorites;
    private ProgressBar mProgressBar;
    private String dbUserId = Sesion.getInstance().getSessionUserId();
    private static FavouritesTab instance = null;
    private static boolean loaded = false;
    private ImageAdapter adapter;

    public FavouritesTab() {
        // Required empty public constructor
    }

    public static FavouritesTab getInstance() {
        if (instance == null) {
            instance = new FavouritesTab();
        }
        return instance;
    }

    private void setGridview() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 3;
        gridview.setColumnWidth(imageWidth);
        gridview.setHorizontalSpacing(8);
        gridview.setNumColumns(3);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
                intent.putExtra("article", favorites.get(position));
                intent.putExtra("position", position);
                startActivityForResult(intent, FAVOURITE_INFO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FAVOURITE_INFO) {
            if (resultCode == RESULT_OK) {
                // TODO agregar funcionalidad a articleinfo o aca -> eliminar de ropero
                // chequear si esta efectivamente guardado en ropero, sino eliminar de la grilla
                data.getExtras().getBoolean("removed");
            }
        }
    }

    private void getFavorites() {
        dB.collection("articles")
                .whereArrayContains("favorites", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Article art = document.toObject(Article.class);
                                art.setArticleId(document.getId());
                                favorites.add(art);
                            }
                            adapter.notifyDataSetChanged();
                            loaded = true;
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gridview = view.findViewById(R.id.grid_favoritos);
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        if (!loaded) {
            setGridview();
            favorites = new ArrayList<>();
            adapter = new ImageAdapter(getActivity(), favorites);
            getFavorites();
        } else {
            adapter.notifyDataSetChanged();
        }
        gridview.setAdapter(adapter);
        mProgressBar.setVisibility(View.INVISIBLE);
        super.onViewCreated(view, savedInstanceState);
    }

    public static void refreshStatus() {
        if (instance != null) {
            loaded = false;
        }
    }

}
