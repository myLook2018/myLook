package com.mylook.mylook.premiumUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.PremiumPublication;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.info.ArticleInfoActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationDetail extends AppCompatActivity{
    private ImageView imgPhoto;
    private TextView txtStoreName;
    private TextView txtDescription;
    private RatingBar ratingBar;
    private CircleImageView imgStore;
    private PremiumPublication publication;
    private Toolbar tb;
    private PremiumUser premiumUser;
    private Article art;
    private Context mContext = PublicationDetail.this;
    private CardView cv;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_detail);
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Post");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        progressBar=findViewById(R.id.progressBarPub);
        progressBar.setVisibility(View.VISIBLE);
        publication=(PremiumPublication)getIntent().getSerializableExtra("publication");
        loadData();
        Log.e("CLICK EN LA FOTO",publication.getPublicationPhoto());

    }

    private void loadData() {
        if(!Strings.isNullOrEmpty(publication.getClientId())){
            Log.e("Entro", "");
            FirebaseFirestore.getInstance().collection("premiumUsers").whereEqualTo("clientId",publication.getClientId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                if(!task.getResult().getDocuments().isEmpty()) {
                                    premiumUser = task.getResult().getDocuments().get(0).toObject(PremiumUser.class);
                                    FirebaseFirestore.getInstance().collection("articles").document(publication.getArticleId()).get()
                                            .addOnCompleteListener(task1 -> {
                                                if(task1.isComplete()){
                                                    art=task1.getResult().toObject(Article.class);
                                                    art.setArticleId(publication.getArticleId());
                                                    if (art != null)
                                                        initElements();
                                                    else {
                                                        displayMessage("No tiene articulo asociado");
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });

        }else{
            this.finish();
        }

    }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void initElements() {

        imgStore=findViewById(R.id.imgStore);
        Glide.with(getApplicationContext()).asBitmap().load(premiumUser.getProfilePhoto()).into(imgStore);
        imgPhoto = findViewById(R.id.imgArticle);
        Glide.with(getApplicationContext()).asBitmap().load(publication.getPublicationPhoto()).into(imgPhoto);
        txtStoreName =findViewById(R.id.txtStore);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.INVISIBLE);
        txtDescription= findViewById(R.id.txtDescription);
        cv=findViewById(R.id.cv);
        TextView lblVisitArticle = findViewById(R.id.lblVisitArticle);
        lblVisitArticle.setVisibility(View.VISIBLE);

        txtStoreName.setText(premiumUser.getUserName());
        txtDescription.setText(art.getTitle() +" de "+art.getStoreName());
        cv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        txtStoreName.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumUserProfileActivity.class);
            Log.d("perfil destacado", "onClick: paso por intent la data del articulo");
            intent.putExtra("clientId", premiumUser.getClientId());
            mContext.startActivity(intent);
        });
        imgPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ArticleInfoActivity.class);
            Log.d("info del articulo", "onClick: paso por intent la data del articulo");
            intent.putExtra("article", art);
            mContext.startActivity(intent);
        });

        lblVisitArticle.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                Log.d("info del articulo", "onClick: paso por intent la data del articulo");
                intent.putExtra("article", art);
                mContext.startActivity(intent);
        });


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
