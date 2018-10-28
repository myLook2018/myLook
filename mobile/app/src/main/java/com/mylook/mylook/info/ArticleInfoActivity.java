package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;
    private ImageButton btnLike;
    private TextView articleStore, articleCost, articleStock, articleColors, articleMaterial, articlesSize, articleTitle;
    private FirebaseFirestore dB;
    private FirebaseUser user;
    private String articleId,downLoadUri;
    private String closetId;
    private ArrayList<String> tags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_article);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dB = FirebaseFirestore.getInstance();
        backArrow = (ImageView) findViewById(R.id.backArrow);
        articleColors = (TextView) findViewById(R.id.lblColors);
        articleCost = (TextView) findViewById(R.id.article_cost);
        articleMaterial = (TextView) findViewById(R.id.lblMaterial);
        articlesSize = (TextView) findViewById(R.id.lblSizes);
        articleStock = (TextView) findViewById(R.id.lblstock);
        articleStore = (TextView) findViewById(R.id.lblstore);
        articleImage = (ImageView) findViewById(R.id.article_image);
        articleTitle=(TextView)findViewById(R.id.lblTitle);
        btnLike =findViewById(R.id.btnLike);

        //retrieve data from intent
        final Intent intent = getIntent();
        Article article= (Article) intent.getSerializableExtra("article");
        articleId=article.getArticleId();
        tags = intent.getStringArrayListExtra("tags");
        Log.e("ROPERO", article.getArticleId());

        downLoadUri=article.getPicture();
        articleStore.setText(article.getStoreName());
        articleCost.setText(String.format("$%s", String.valueOf(article.getCost())));
        articleStock.setText(String.format("Stock: %s", String.valueOf(article.getInitial_stock())));
        articleColors.setText(String.format("Colores: %s", article.getColors().get(0)));
        articleMaterial.setText(String.format("Material: %s", article.getMaterial()));
        articlesSize.setText(String.format("Talles: %s", article.getSizes().get(0)));
        articleTitle.setText(article.getTitle());

        Glide.with(mContext).load(downLoadUri).into(articleImage);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        articleStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                Log.d("Nombre tienda", "onClick: Paso el nombre de la tienda: " + intent.getStringExtra("Tienda"));
                intentVisitStore.putExtra("Tienda", intent.getStringExtra("Tienda"));
                mContext.startActivity(intentVisitStore);
            }
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOnCloset();
            }
        });


    }

    private void saveOnCloset() {

        final Map<String, Object> favorites = new HashMap<>();
        favorites.put("articleId", articleId);
        favorites.put("downloadUri", downLoadUri);
        favorites.put("collecion", null);

        dB.collection("closets")
                .whereEqualTo("userID",user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().getDocuments().size()>0){
                            closetId=task.getResult().getDocuments().get(0).getId();
                            dB.collection("closets").document(closetId).collection("favorites")
                                    .whereEqualTo("articleId",articleId).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.getResult().getDocuments().size()==0){
                                                Log.e("CLOSET", closetId);
                                                dB.collection("closets").document(closetId).collection("favorites").add(favorites);
                                                sendNewInteraction();
                                                displayMessage("Se añadió a tu ropero");
                                            }else
                                            {
                                                displayMessage("Ya es favorito");
                                            }
                                        }
                                    });
                        }else
                        {
                            displayMessage("No tienes un ropero asociado");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        displayMessage("Error al guardar en ropero");
                    }
                });


    }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNewInteraction(){
        Interaction userInteraction = new Interaction();
        userInteraction.setSavedToCloset(false);
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(true);
        userInteraction.setArticleId(this.articleId);
        userInteraction.setStoreName(this.articleStore.getText().toString());
        userInteraction.setTags(tags);
        userInteraction.setUserId(user.getUid());
        dB.collection("interactions").add(userInteraction);
    }


}
