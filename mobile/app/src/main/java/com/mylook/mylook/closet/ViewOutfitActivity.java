package com.mylook.mylook.closet;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;

import java.util.HashMap;

public class ViewOutfitActivity extends AppCompatActivity {
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private HashMap<String, String> outfitItems;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar tb;
    private String collectionName, category, outfitId;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_outfit);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mProgressBar = findViewById(R.id.mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        initElements();
        loadOutfit();
    }

    private void loadOutfit() {
        for (final String item : outfitItems.keySet()) {
            String articleId = outfitItems.get(item);
            loadImage(item, outfitItems.get(item), articleId);
        }
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    private void loadImage(final String item,final String picture, String articleId){
        dB.collection("articles").document(articleId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            View v = null;
                            if(item.getClass().equals( Integer.class))
                                v = findViewById(Integer.parseInt(item));
                            if(v == null) {
                                v = findViewById(getResources().getIdentifier(item, "id", getApplicationContext().getPackageName()));
                            }
                            String art = (String)task.getResult().get("picture");
                            Glide.with(ViewOutfitActivity.this).asBitmap().load(art)
                                    .into((ImageView) v);

                        }
                    }
                });
    }

    private void initElements() {
        collectionName = getIntent().getExtras().get("name").toString();
        category = getIntent().getExtras().get("category").toString();
        outfitItems = (HashMap<String, String>) getIntent().getExtras().get("items");
        outfitId = getIntent().getExtras().get("id").toString();

        tb = findViewById(R.id.toolbar);
        tb.setTitle(collectionName);
        setSupportActionBar(tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.outfit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_outfit) {
            deleteAlert();
        } else if(id == R.id.edit_outfit){
            /* TODO
            Intent intent = new Intent(getApplicationContext(), CreateOutfitActivity.class);
            intent.putExtra("name",collectionName);
            intent.putExtra("category",category);
            intent.putExtra("id",outfitId);
            startActivity(intent);
            finish();
            */
        } else if (id==R.id.home) {


        } else{
            onBackPressed();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAlert(){
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ViewOutfitActivity.this, R.style.AlertDialogTheme);

        final android.app.AlertDialog alert = dialog.setTitle("Eliminar conjunto")
                .setMessage("¿Estás seguro que querés eliminar este conjunto?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        deleteOutfit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.cancel();
                    }
                }).create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple));
                alert.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
            }
        });
        alert.show();
    }

    private void deleteOutfit(){
        dB.collection("closets").whereEqualTo("userID", user.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        task.getResult().getDocuments().get(0).getReference().collection("outfits").document(outfitId)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Tu conjunto fue eliminado", Toast.LENGTH_SHORT).show();
                                ViewOutfitActivity.this.finish();
                            }
                        });
                    }
                }
        );
    }
}
