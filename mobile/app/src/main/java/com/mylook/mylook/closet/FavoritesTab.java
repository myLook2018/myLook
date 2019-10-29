package com.mylook.mylook.closet;

import android.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ArticlesGridAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class FavoritesTab extends Fragment {

    public static final int CLOSET_FAVORITE_REQUEST = 1;

    private GridView favoritesGridView;
    private ArticlesGridAdapter adapter;
    private static ClosetModel closet;
    private ProgressBar mProgressBar;

    public static void refreshStatus() {
        if(closet!=null){
            closet.reloadFavorites();
        }
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
        startActivityForResult(new Intent(getContext(), ArticleInfoActivity.class)
                .putExtra("article", adapter.getItem(position)), CLOSET_FAVORITE_REQUEST);
    }

    private void confirmDeleteFavorite(int position) {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setTitle("Eliminar favorito")
                .setMessage("Estás seguro de que querés eliminar el favorito?")
                .setPositiveButton("Eliminar", (paramDialogInterface, paramInt) -> {
                          deleteFavorite(position);
                        }
                )
                .setNegativeButton("Cancelar", null).create();
        alert.setOnShowListener(dialog1 -> {
            alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.purple));
            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.purple));
        });
        alert.show();

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
                                .addOnSuccessListener(success2 -> {
                                    adapter.notifyDataSetChanged();
                                    displayToast("Se eliminó de tus favoritos y actualizaron conjuntos");
                                })
                                .addOnFailureListener(fail -> {
                                    adapter.notifyDataSetChanged();
                                    displayToast("Se eliminó de tus favoritos, pero los conjuntos no se actualizaron");
                                });
                    } else {
                        adapter.notifyDataSetChanged();
                        displayToast("Se eliminó de tu favorito");
                    }
                })
                .addOnFailureListener(fail -> displayToast("Error al eliminar de los favoritos"));
    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tmr", "onActivityResult: " + "start");
        if (requestCode == CLOSET_FAVORITE_REQUEST) {
            Log.d("tmr", "onActivityResult: " + "request");
            if (resultCode == ArticleInfoActivity.RESULT_OK) {
                Log.d("tmr", "onActivityResult: " + "result ok");
                if (data.getBooleanExtra("removed", false)) {
                    Log.d("tmr", "onActivityResult: " + "removed");
                    closet.load();
                }
            }
        }
    }
}