package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ImageAdapter;

public class FavouritesTab extends Fragment {

    private GridView gridview;
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private ImageAdapter adapter;
    private FavoritesViewModel favoritesViewModel;
    private ProgressBar mProgressBar;

    public FavouritesTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        favoritesViewModel = ViewModelProviders.of(getParentFragment()).get(FavoritesViewModel.class);
        favoritesViewModel.getFavorites().observe(this, favorites -> {
            adapter = new ImageAdapter(getActivity(), favoritesViewModel.getFavorites().getValue());
            //adapter.notifyDataSetChanged();
            //mProgressBar.setVisibility(View.INVISIBLE);
            if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        gridview = view.findViewById(R.id.grid_favoritos);
        gridview.setAdapter(adapter);
        setGridView();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setGridView() {
        int widthGrid = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = widthGrid / 3;
        gridview.setColumnWidth(imageWidth);
        gridview.setHorizontalSpacing(8);
        gridview.setNumColumns(3);
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
            intent.putExtra("article",
                    favoritesViewModel.getFavorites().getValue().get(position));
            startActivity(intent);
        });
        gridview.setOnItemLongClickListener((parent, v, position, id) -> {
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Eliminar de ropero?")
                    .setMessage("El artículo desaparecerá de tu ropero.")
                    .setPositiveButton("Eliminar", (dialog, which) -> removeFavorite(position))
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
    }

    private void removeFavorite(final int position) {
        dB.collection("articles")
                .document(favoritesViewModel.getFavorites().getValue().get(position).getArticleId())
                .update("favorites",
                        FieldValue.arrayRemove(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoritesViewModel.getFavorites().getValue().remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Se eliminó de tu ropero",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al eliminar del ropero",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
