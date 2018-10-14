package com.mylook.mylook.recommend;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;

public class RequestRecommendActivity extends AppCompatActivity{

    private static final String TAG = "RequestRecommendationA";
    private ImageView imgRequestPhoto;
    private TextView txtDescription;


    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation);
        Log.d(TAG, "onCreate: started.");
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Tu solicitud");
        setSupportActionBar(tb);
        imgRequestPhoto= findViewById(R.id.imgRequestPhoto);
        txtDescription=findViewById(R.id.txtDescription);
        getIncomingIntent();
    }

    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        if(getIntent().hasExtra("requestRecommendation")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            RequestRecommendation requestRecommendation= (RequestRecommendation) getIntent().getSerializableExtra("requestRecommendation");
            txtDescription.setText(requestRecommendation.getDescription());
            setImage(requestRecommendation.getRequestPhoto());
        }
    }

    private void setImage(String imageUrl){
        Log.d(TAG, "setImage: setting te image and name to widgets.");

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(imgRequestPhoto);
    }

}

