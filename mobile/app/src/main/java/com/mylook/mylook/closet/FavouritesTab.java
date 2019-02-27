package com.mylook.mylook.closet;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Closet;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.ArrayList;


public class FavouritesTab extends Fragment {

    private GridView gridview;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Closet closet;
    private ArrayList<Favorite> favorites;
    private Activity act;
    private ProgressBar mProgressBar;

    public FavouritesTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favorites = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();
        getCloset();
        setClickListener();
    }

    private void setClickListener(){
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                dB.collection("articles").document((String) parent.getAdapter().getItem(position)).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    String articleId = task.getResult().getId();
                                    Article art = task.getResult().toObject(Article.class);
                                    art.setArticleId(articleId);
                                    Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
                                    intent.putExtra("article", art);
                                    getContext().startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "No se ha podido cargar tus favoritos", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void setGridview() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 3;
        gridview.setColumnWidth(imageWidth);
        gridview.setHorizontalSpacing(8);
        gridview.setNumColumns(3);
        getCloset();
        setClickListener();
    }

    private void getCloset() {
        dB.collection("closets")
                .whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                closet = document.toObject(Closet.class);
                                String id = document.getId();
                                dB.collection("closets").document(id).collection("favorites").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<String> arrayList = new ArrayList<>();
                                                    favorites = new ArrayList<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Favorite fav = documentSnapshot.toObject(Favorite.class);
                                                        favorites.add(fav);
                                                        arrayList.add(fav.getDownloadUri());
                                                    }
                                                    gridview.setAdapter(new com.mylook.mylook.utils.ImageAdapter(act, favorites));
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    return;
                                                } else
                                                    Log.e("FAVORITES", "Nuuuuuuuuuuuuuuuuuuuuuu");
                                            }
                                        });
                            }
                        } else {
                            Log.e("FAVORITES", "NOOOOOOOOOOOOO");
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        act = getActivity();
        return inflater.inflate(R.layout.tab_favourites, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gridview = view.findViewById(R.id.grid_favoritos);
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        setGridview();

        super.onViewCreated(view, savedInstanceState);
    }


    public ArrayList<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<Favorite> favorites) {
        this.favorites = favorites;
    }



}
