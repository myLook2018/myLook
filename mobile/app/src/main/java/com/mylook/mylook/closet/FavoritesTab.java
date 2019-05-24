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

import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ArticlesGridAdapter;

import java.io.Serializable;

public class FavoritesTab extends Fragment {

    private GridView gridview;
    private ArticlesGridAdapter adapter;
    private ClosetModel closet;
    private ProgressBar mProgressBar;

    public FavoritesTab() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArticlesGridAdapter(getContext(), null);
        closet = ViewModelProviders.of(getParentFragment()).get(ClosetModel.class);
        closet.getFavorites().observe(this, favorites -> {
            adapter.setArticles(favorites);
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        });
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
        gridview = view.findViewById(R.id.grid_favoritos);
        setGridView();
    }

    private void setGridView() {
        gridview.setAdapter(adapter);
        gridview.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridview.setHorizontalSpacing(8);
        gridview.setNumColumns(3);
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            Intent intent = new Intent(getContext(), ArticleInfoActivity.class);
            startActivity(intent.putExtra("article",
                    (Article) adapter.getItem(position)));
        });
        //TODO ver si agrego este metodo para agregar a conjunto como "playlist"
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
        int res = closet.removeFavorite(position);
        switch (res) {
            case -1:
                displayToast("Error al eliminar del ropero");
                break;
            case 0:
                displayToast("Se eliminó de tu ropero");
                adapter.notifyDataSetChanged();
                break;
            default:
                displayToast("Se eliminó de tu ropero y actualizaron " + res + " conjuntos");
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
