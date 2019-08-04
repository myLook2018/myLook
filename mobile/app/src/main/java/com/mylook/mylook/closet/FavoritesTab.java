package com.mylook.mylook.closet;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ArticlesGridAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoritesTab extends Fragment {

    private GridView favoritesGridView;
    private ArticlesGridAdapter adapter;
    private static ClosetModel closet;
    private ProgressBar mProgressBar;

    public static void refreshStatus() {
            closet.reloadFavorites();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closet = ViewModelProviders.of(getParentFragment()).get(ClosetModel.class);
        closet.load();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        favoritesGridView = view.findViewById(R.id.grid_favoritos);
        favoritesGridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        favoritesGridView.setHorizontalSpacing(8);
        favoritesGridView.setNumColumns(3);
        favoritesGridView.setOnItemClickListener((parent, v, position, id) -> showFavorite(position));
        favoritesGridView.setOnItemLongClickListener((parent, v, position, id) -> favoriteOptions(position));
        closet.getFavorites().observe(this, favorites -> {
            adapter = new ArticlesGridAdapter(getContext(), favorites);
            favoritesGridView.setAdapter(adapter);
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        });
    }

    private boolean favoriteOptions(int position) {
        String[] options = {"Ver", "Eliminar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Favorito");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showFavorite(position);
                    break;
                case 1:
                    confirmDeleteFavorite(position);
                    break;
                default:
                    break;
            }
        });
        builder.show();
        return true;
    }

    private void showFavorite(int position) {
        startActivity(new Intent(getContext(), ArticleInfoActivity.class)
                .putExtra("article", adapter.getItem(position)));
    }

    private void confirmDeleteFavorite(int position) {
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar favorito")
                .setMessage("Estás seguro de que querés eliminar el favorito?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteFavorite(position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteFavorite(int position) {
        String idToDelete = adapter.getItem(position).getArticleId();
        mProgressBar.setVisibility(View.VISIBLE);
        closet.removeFavorite(position)
                .addOnSuccessListener(success1 -> {
                    List<String> outfitsToChange = closet.getOutfits().getValue().stream()
                            .filter(o -> o.getFavorites().contains(idToDelete))
                            .map(Outfit::getOutfitId).collect(Collectors.toList());
                    if (outfitsToChange.size() != 0) {
                        closet.removeFavoriteFromOutfits(outfitsToChange, idToDelete)
                                .addOnSuccessListener(success2 ->
                                        displayToast("Se eliminó de tu ropero y actualizaron conjuntos"))
                                .addOnFailureListener(fail ->
                                        displayToast("Se eliminó de tu ropero, pero los conjuntos no se actualizaron"));
                    } else {
                        displayToast("Se eliminó de tu ropero");
                    }
                })
                .addOnFailureListener(fail -> displayToast("Error al eliminar del ropero"));
    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
