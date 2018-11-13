package com.mylook.mylook.premiumUser;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumPublication;

public class PublicationDetail extends Activity{
    private ImageView imgPhoto;
    private TextView txtStoreName;
    private TextView txtDescription;
    private RatingBar ratingBar;
    private ImageView imgStore;
    private PremiumPublication publication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication_detail);

        publication=(PremiumPublication)getIntent().getParcelableExtra("publication");
        initElements();
    }

    private void initElements() {
        imgPhoto = findViewById(R.id.imgArticle);
        txtStoreName =findViewById(R.id.txtStore);
        ratingBar = findViewById(R.id.ratingBar);
        txtDescription= findViewById(R.id.txtDescription);
        imgStore=findViewById(R.id.imgStore);


        Glide.with(getApplicationContext()).asBitmap().load(publication.getPublicationPhoto()).into(imgPhoto);
        txtDescription.setText(publication.getStoreNme());



    }
}
