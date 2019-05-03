package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.utils.ImageAdapter;

import java.util.ArrayList;


public class FavouritesTab extends Fragment {

    private GridView gridview;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ArrayList<Favorite> favorites;
    private ArrayList<Favorite> selectedFavorites; //para la creacion de conjunto
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
        setClickListener();
    }

    private void setClickListener() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dB.collection("articles").document(((Favorite) parent.getAdapter().getItem(position)).getArticleId()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Article art = task.getResult().toObject(Article.class);
                                    art.setArticleId(task.getResult().getId());
                                    Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
                                    intent.putExtra("article", art);
                                    getContext().startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "No se han podido cargar tus favoritos", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                if (selectedFavorites == null) {
                    selectedFavorites = new ArrayList();
                }
                Log.d("LONGCLICKED", Boolean.toString(v.isSelected()));
                selectedFavorites.add((Favorite) parent.getAdapter().getItem(position));
                Log.d("LONGCLICKED", Boolean.toString(v.isSelected()));
                Log.d("LONGCLICKED", Integer.toString(selectedFavorites.size()));
                return v.isSelected();
            }
        });
    }

    private void getCloset() {
        //TODO cambiar estructura de firestore para dar soporte a una única consulta
        dB.collection("closets")
                .whereEqualTo("userID", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("favorites").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Favorite fav = documentSnapshot.toObject(Favorite.class);
                                                        favorites.add(fav);
                                                        adapter.notifyDataSetChanged();
                                                        loaded = true;
                                                    }
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    return;
                                                }
                                            }
                                        });
                            }
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
            getCloset();
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
