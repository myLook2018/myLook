package com.mylook.mylook.premiumUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.info.ArticleInfoActivity;

public class PublicationDetail extends AppCompatActivity{
    private ImageView imgPhoto;
    private TextView txtStoreName;
    private TextView txtDescription;
    private RatingBar ratingBar;
    private ImageView imgStore;
    private PremiumPublication publication;
    private Toolbar tb;
    private FirebaseFirestore dB;
    private PremiumUser premiumUser;
    private Article art;
    private Context mContext = PublicationDetail.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_detail);
        dB = FirebaseFirestore.getInstance();

        tb = findViewById(R.id.toolbar);
        tb.setTitle("Editar Información");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        publication=(PremiumPublication)getIntent().getSerializableExtra("publication");
        loadData();
        Log.e("CLICK EN LA FOTO",publication.getPublicationPhoto());

    }

    private void loadData() {
    dB.collection("premiumUsers").whereEqualTo("clientId",publication.getClientId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            premiumUser=(PremiumUser)task.getResult().getDocuments().get(0).toObject(PremiumUser.class);
                            dB.collection("articles").whereEqualTo("code",publication.getArticleCode()).whereEqualTo("storeName",publication.getStoreName())
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(!(task.getResult().getDocuments().isEmpty())) {
                                            art = task.getResult().getDocuments().get(0).toObject(Article.class);
                                            art.setArticleId(task.getResult().getDocuments().get(0).getId());
                                            if(art!=null)
                                                initElements();
                                            else{
                                                displayMessage("No tiene articulo asociado");
                                                finish();
                                            }
                                        }
                                    }
                                }});
                        }
                    }
                });
        }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void initElements() {
        imgPhoto = findViewById(R.id.imgArticle);
        txtStoreName =findViewById(R.id.txtStore);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.INVISIBLE);
        txtDescription= findViewById(R.id.txtDescription);
        imgStore=findViewById(R.id.imgStore);


        Glide.with(getApplicationContext()).asBitmap().load(publication.getPublicationPhoto()).into(imgPhoto);
        txtDescription.setText(publication.getStoreName());
        txtStoreName.setText(premiumUser.getUserName());
        Glide.with(getApplicationContext()).asBitmap().load(premiumUser.getProfilePhoto()).into(imgStore);
        txtDescription.setText(art.getTitle() +" de "+art.getStoreName());

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                intent.putExtra("article", art);
                mContext.startActivity(intent);
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
