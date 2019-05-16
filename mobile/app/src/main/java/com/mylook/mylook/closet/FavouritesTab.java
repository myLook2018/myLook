package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ImageAdapter;

import java.util.ArrayList;

public class FavouritesTab extends Fragment {

    private static final int FAVOURITE_INFO = 1;
    private GridView gridview;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ProgressBar mProgressBar;
    private String dbUserId = Sesion.getInstance().getSessionUserId();
    private ImageAdapter adapter;
    private ArrayList<Article> favorites;
    private FavoritesViewModel favoritesHolder;

    public FavouritesTab() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gridview = view.findViewById(R.id.grid_favoritos);
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        adapter = new ImageAdapter(getActivity(), FavoritesViewModel.getFavorites());
        gridview.setAdapter(adapter);
        setGridView();
        getFavorites();
        mProgressBar.setVisibility(View.INVISIBLE);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setGridView() {
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
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                                           final int position, long id) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar de ropero?")
                        .setMessage("El artículo desaparecerá de tu ropero.")
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeFavorite(position);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            }
        });
    }

    private void getFavorites() {
        dB.collection("articles")
                .whereArrayContains("favorites", dbUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FavoritesViewModel.getInstance().createFavorites(task.getResult());
                            adapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void removeFavorite(final int position) {
        dB.collection("articles").document(FavoritesViewModel.getFavorites().get(position).getArticleId())
                .update("favorites", FieldValue.arrayRemove(dbUserId))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FavoritesViewModel.getFavorites().remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Se eliminó de tu ropero", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar del ropero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
