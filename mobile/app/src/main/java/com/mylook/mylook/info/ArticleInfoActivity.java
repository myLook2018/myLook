package com.mylook.mylook.info;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.Interaction;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.session.Sesion;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleInfoActivity extends AppCompatActivity {

    private Context mContext = ArticleInfoActivity.this;
    private ImageView backArrow, articleImage;

    private ExpandableListView expandableListView;
    private FloatingActionButton btnCloset;
    private FloatingActionButton btnShare;
    private String articleId,closetId;
    private ArrayList<String> tags;
    private String downLoadUri, dbUserId;
    private Article article;
    private FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();;
    private FirebaseFirestore dB =FirebaseFirestore.getInstance();
    private TextView txtTitle;
    private TextView txtStoreName;
    private LinearLayout lnlSizes;
    private LinearLayout lnlColors;
    private TextView txtMaterial;
    private TextView txtCost;
    private ShareActionProvider mShareActionProvider;
    private boolean fromDeepLink = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_article_collapsing);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_more_info);
        invalidateOptionsMenu();
        getArticleFromIntent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(fromDeepLink){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void getUserId() {
        dB.collection("clients").whereEqualTo("userId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                    dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                    downLoadUri=article.getPicture();
                    initElements();
                    setDetail();
                } else {
                    dB.collection("clients").whereEqualTo("email", user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                dbUserId = task.getResult().getDocuments().get(0).get("userId").toString();
                                downLoadUri=article.getPicture();
                                initElements();
                                setDetail();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setDetail() {
        txtStoreName.setText(article.getStoreName());
        txtTitle.setText(article.getTitle());
        txtMaterial.setText(article.getMaterial());
        txtCost.setText(String.format("$%s", String.valueOf(article.getCost())));
        LinearLayout lnl=new LinearLayout(this);
        lnl.setOrientation(LinearLayout.VERTICAL);
        for (String size:article.getSizes()){
            TextView item=new TextView(this);
            item.setText(size);
            lnl.addView(item);
        }
        lnlSizes.addView(lnl);
        LinearLayout lnl2=new LinearLayout(this);
        for (String color:article.getColors()){
            TextView item=new TextView(this);
            item.setText(color);
            lnl2.addView(item);
        }
        lnlColors.addView(lnl2);
        lnl2.setOrientation(LinearLayout.VERTICAL);
        txtStoreName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisitStore = new Intent(mContext, StoreActivity.class);
                Log.d("Nombre tienda", "onClick: Paso el nombre de la tienda: " );
                intentVisitStore.putExtra("Tienda", article.getStoreName());
                mContext.startActivity(intentVisitStore);
            }
        });
    }

    private void initElements() {
        backArrow = findViewById(R.id.backArrow);
        btnCloset=findViewById(R.id.btnCloset);
        articleImage=findViewById(R.id.article_image);
        txtTitle=findViewById(R.id.txtTitle);
        txtStoreName=findViewById(R.id.txtStoreName);
        txtMaterial=findViewById(R.id.txtMaterial);
        txtCost=findViewById(R.id.txtCost);
        lnlSizes=findViewById(R.id.lnlSizes);
        lnlColors=findViewById(R.id.lnlColors);
        btnShare =  findViewById(R.id.btnShare);
        Glide.with(mContext).load(downLoadUri).into(articleImage);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCloset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOnCloset();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareArticle();
            }
        });

    }

    private void shareArticle(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Mirá esta prenda! https://www.mylook.com/article?articleId="+articleId);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via"));
    }

    private void getArticleFromIntent(){
        //retrieve data from intent
        Intent intent = getIntent();
        if(intent.hasExtra("article")) {
            article= (Article) intent.getSerializableExtra("article");
            articleId=article.getArticleId();
            fromDeepLink = false;
            getUserId();
        } else{
            fromDeepLink = true;
            try {
                articleId = intent.getData().getQueryParameter("articleId");
            } catch (Exception e){
                articleId = intent.getStringExtra("articleId");
            }
            getArticleFromId(articleId);
        }
    }

    private void getArticleFromId(String id){
        dB.collection("articles").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                article = task.getResult().toObject(Article.class);
                getUserId();
            }
        });
    }



    private void saveOnCloset() {

        final Map<String, Object> favorites = new HashMap<>();
        favorites.put("articleId", articleId);
        favorites.put("downloadUri", downLoadUri);
        favorites.put("collecion", null);

        dB.collection("closets")
                .whereEqualTo("userID",dbUserId)
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
                                                Sesion.getInstance().updateActivitiesStatus(Sesion.HOME_FRAGMENT, Sesion.CLOSET_FRAGMENT);
                                            }else
                                            {
                                                displayMessage("Ya está en tu ropero");
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
        Log.e("PROMOTION LEVEL",article.getPromotionLevel()+"");
        userInteraction.setPromotionLevel(article.getPromotionLevel());
        userInteraction.setSavedToCloset(true);
        userInteraction.setLiked(false);
        userInteraction.setClickOnArticle(false);
        userInteraction.setArticleId(this.articleId);
        userInteraction.setStoreName(this.article.getStoreName());
        userInteraction.setTags(tags);
        userInteraction.setUserId(user.getUid());
        dB.collection("interactions").add(userInteraction);
    }


}
