package com.mylook.mylook.closet;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.entities.Outfit;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.utils.ArticlesGridAdapter;

import static com.mylook.mylook.closet.OutfitCreateEditActivity.OUTFIT_EDIT_REQUEST;

public class OutfitInfoActivity extends AppCompatActivity {

    static final int ARTICLE_INFO_REQUEST = 3;
    static final int OUTFIT_INFO_REQUEST = 1;
    static final int OUTFIT_DELETED = 1;
    static final int OUTFIT_EDITED = 2;
    private GridView gridView;
    private ArticlesGridAdapter adapter;
    private ProgressBar progressBar;
    private Outfit outfit;
    private TextView emptyTitleTextView;
    private TextView emptyInfoTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfit_articles_layout);
        initElements();
        getOutfitFromIntent();
        fillGrid();
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.outfit_articles_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.outfit_articles_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        gridView = findViewById(R.id.outfit_articles_grid);
        emptyTitleTextView = findViewById(R.id.outfit_articles_empty_title);
        emptyInfoTextView = findViewById(R.id.outfit_articles_empty_info);
    }

    private void getOutfitFromIntent() {
        Intent intent = getIntent();
        outfit = (Outfit) intent.getSerializableExtra("outfit");
        if (!outfit.getArticles().isEmpty()) {
            emptyTitleTextView.setVisibility(View.GONE);
            emptyInfoTextView.setVisibility(View.GONE);
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle(outfit.getName());
        adapter = new ArticlesGridAdapter(this, outfit.getArticles());
        gridView.setAdapter(adapter);
    }

    private void fillGrid() {
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setHorizontalSpacing(8);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener((parent, v, position, id) -> showFavorite(position));
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.outfit_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit_outfit:
                editOutfit();
                return true;
            case R.id.delete_outfit:
                deleteOutfit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editOutfit() {
        startActivityForResult(new Intent(this, OutfitCreateEditActivity.class)
                .putExtra("outfit", outfit)
                .putExtra("create", false), OUTFIT_EDIT_REQUEST);
    }

    private void deleteOutfit() {
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle("Eliminar conjunto")
                .setMessage("Estás seguro de que querés eliminar el conjunto?")
                .setPositiveButton("Si", (paramDialogInterface, paramInt) -> {
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseFirestore.getInstance().collection("outfits")
                                    .document(outfit.getOutfitId()).delete()
                                    .addOnSuccessListener(task -> {
                                        setResult(OUTFIT_DELETED, new Intent().putExtra("outfit", outfit));
                                        finish();
                                    })
                                    .addOnFailureListener(task ->
                                            displayToast("Error al eliminar conjunto"));
                        }
                )
                .setNegativeButton("No", null).create();
        alert.setOnShowListener(dialog1 -> {
            alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.purple));
            alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.purple));
        });
        alert.show();
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showFavorite(int position) {
        startActivityForResult(new Intent(this, ArticleInfoActivity.class)
                .putExtra("article", adapter.getItem(position)), ARTICLE_INFO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OUTFIT_EDIT_REQUEST) {
            if (resultCode == OUTFIT_EDITED) {
                progressBar.setVisibility(View.VISIBLE);
                outfit = (Outfit) (data != null ? data.getSerializableExtra("outfit") : null);
                if (outfit != null) {
                    ActionBar ab = getSupportActionBar();
                    if (ab != null) ab.setTitle(outfit.getName());
                    gridView.setAdapter(new ArticlesGridAdapter(this, outfit.getArticles()));
                } else {
                    displayToast("Error al mostrar los cambios en el conjunto");
                }
                setResult(OUTFIT_EDITED, data);
                progressBar.setVisibility(View.GONE);
            }
        } else if (requestCode == ARTICLE_INFO_REQUEST) {
            // TODO not working
            if (resultCode == ArticleInfoActivity.RESULT_OK) {
                if (data.getBooleanExtra("removed", false)) {
                    String id = data.getStringExtra("id");
                    if (outfit.getFavorites().remove(id)) {
                        outfit.getArticles().removeIf(art -> art.getArticleId().equals(id));
                    }
                    gridView.setAdapter(new ArticlesGridAdapter(this, outfit.getArticles()));
                }
            }
        }
    }
}
