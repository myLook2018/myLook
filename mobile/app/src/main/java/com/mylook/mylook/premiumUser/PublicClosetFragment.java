package com.mylook.mylook.premiumUser;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Favorite;
import com.mylook.mylook.utils.GridImageAdapter;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class PublicClosetFragment extends Fragment {

    private  FirebaseFirestore dB=null;
    private  String premiumUserId;
    private Closet closet;
    private ArrayList<Article> favorites;


    public PublicClosetFragment() {
    }

    @SuppressLint("ValidFragment")
    public PublicClosetFragment(String premiumUserId) {
        dB = FirebaseFirestore.getInstance();
        this.premiumUserId=premiumUserId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_catalog, container, false);

        // Obtención del grid view
        GridViewWithHeaderAndFooter grid = rootView.findViewById(R.id.gridview);
        // Inicializar el grid view
        setupGridView(grid);
        return rootView;
    }


    private void setupGridView(final GridViewWithHeaderAndFooter grid) {
        dB.collection("closets")
                .whereEqualTo("userID", premiumUserId)
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
                                                    favorites = new ArrayList<>();
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        Favorite fav = documentSnapshot.toObject(Favorite.class);
                                                        dB.collection("articles").document(fav.getArticleId()).get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            if(task.getResult().exists()){
                                                                            Article art=task.getResult().toObject(Article.class);
                                                                            art.setArticleId(task.getResult().getId());
                                                                            favorites.add(art);
                                                                            }
                                                                        }

                                                                    }
                                                                });
                                                    }
                                                    grid.setAdapter(new GridImageAdapter( getActivity(),R.layout.layout_grid_imageview,favorites));
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
}
