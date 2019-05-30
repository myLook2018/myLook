package com.mylook.mylook.closet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Outfit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class OutfitCreateEditActivity extends AppCompatActivity {

    public static final int OUTFIT_CREATE_REQUEST = 2;
    public static final int OUTFIT_EDIT_REQUEST = 3;
    public static final int OUTFIT_CREATED = 1;
    public static final int OUTFIT_EDITED = 2;
    public static final int OUTFIT_UNCHANGED = 3;
    private GridView gridView;
    private OutfitCreateEditAdapter adapter;
    private ProgressBar progressBar;
    private EditText editText;
    private String outfitDocument;
    private List<Integer> selectedIndexes;
    private List<Article> all;
    private List<String> selectedIds;
    private String mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfit_create_edit_layout);
        initElements();
        getAll();
    }

    private void initElements() {
        Toolbar tb = findViewById(R.id.outfit_create_edit_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.outfit_create_edit_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        editText = findViewById(R.id.outfit_create_edit_name);
        gridView = findViewById(R.id.outfit_create_edit_grid);
        gridView.setColumnWidth(getResources().getDisplayMetrics().widthPixels / 3);
        gridView.setHorizontalSpacing(8);
        gridView.setOnItemClickListener((parent, v, position, id) -> selectForOutfit(v, position));
    }

    private void getAll() {
        all = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("articles")
                .whereArrayContains("favorites", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        all.addAll(task.getResult().getDocuments().stream().map(document -> {
                            Article art = document.toObject(Article.class);
                            if (art != null) {
                                art.setArticleId(document.getId());
                            }
                            return art;
                        }).collect(Collectors.toList()));
                    }
                    getDataFromIntent();
                    adapter = new OutfitCreateEditAdapter(this, all, selectedIndexes);
                    adapter.getSelected().forEach(art -> Log.d("", "selectForOutfit: selectedIndexes " + art));
                    gridView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        ActionBar ab = getSupportActionBar();
        selectedIndexes = new ArrayList<>();
        mode = intent.getBooleanExtra("create", true) ? "create" : "edit";
        if (mode.equals("create")) {
            if (ab != null) ab.setTitle("Nuevo conjunto");
            selectedIds = new ArrayList<>();
        } else {
            Outfit outfit = (Outfit) intent.getSerializableExtra("outfit");
            outfitDocument = outfit.getOutfitId();
            selectedIds = outfit.getArticles().stream()
                    .map(Article::getArticleId).collect(Collectors.toList());
            for (int i = 0; i < all.size(); i++) {
                if (selectedIds.contains(all.get(i).getArticleId())) {
                    selectedIndexes.add(i);
                }
            }
            editText.setText(outfit.getName());
            if (ab != null) ab.setTitle("Editar conjunto");
        }
    }

    private void selectForOutfit(View v, int position) {
        adapter.getSelected().forEach(art -> Log.d("", "selectForOutfit: selectedIndexes " + position));
        String id = adapter.getItem(position).getArticleId();
        if (adapter.getSelected().contains(position)) {
            Log.d("", "selectForOutfit: not selectedIndexes");
            adapter.getSelected().remove(Integer.valueOf(position));
            selectedIds.remove(id);
            ((SelectableImageView) v).displayAsSelected(false);
        } else {
            Log.d("", "selectForOutfit: selectedIndexes");
            adapter.getSelected().add(position);
            selectedIds.add(id);
            ((SelectableImageView) v).displayAsSelected(true);
        }
        adapter.getSelected().forEach(art -> Log.d("", "selectForOutfit: selectedIndexes " + position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(OUTFIT_UNCHANGED);
                this.finish();
                return true;
            case R.id.action_confirm:
                boolean res;
                if (mode.equals("create")) {
                    res = createOutfit();
                } else {
                    res = editOutfit();
                }
                if (res) {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean createOutfit() {
        if (editText.getText().toString().equals("")) {
            displayToast("Ingrese un nombre para el conjunto");
            return false;
        } else {
            HashMap<String, Object> data = new HashMap<>();
            //TODO cambiar categoria (capaz sacarlo)
            data.put("name", editText.getText());
            data.put("category", "");
            data.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.put("favorites", selectedIds);
            if (FirebaseFirestore.getInstance().collection("outfits")
                    .add(data).isSuccessful()) {
                displayToast("Conjunto " + editText.getText() + " creado");
                setResult(OUTFIT_CREATED);
                return true;
            } else {
                displayToast("Error al crear el conjunto");
                return false;
            }
        }
    }

    private boolean editOutfit() {
        if (editText.getText().toString().equals("")) {
            displayToast("Ingrese un nombre para el conjunto");
            return false;
        } else {
            HashMap<String, Object> data = new HashMap<>();
            //TODO cambiar categoria (capaz sacarlo)
            data.put("name", editText.getText().toString());
            data.put("category", "");
            data.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.put("favorites", selectedIds);
            if (FirebaseFirestore.getInstance().collection("outfits")
                    .document(outfitDocument).set(data).isSuccessful()) {
                displayToast("Conjunto " + editText.getText() + " editado");
                setResult(OUTFIT_EDITED);
                return true;
            } else {
                displayToast("Error al editar el conjunto");
                return false;
            }
        }
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
