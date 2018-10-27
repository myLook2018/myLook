package com.mylook.mylook.recommend;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestRecommendActivity extends AppCompatActivity {

    private static final String TAG = "RequestRecommendationA";
    private ImageView imgRequestPhoto;
    private TextView txtDescription;
    private TextView txtTitle;
    private RecyclerView recyclerView;
    private TextView txtLimitDate;
    private ArrayList<HashMap<String, String>> answers;
    private FirebaseFirestore dB;
    private String requestId;


    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation);
        Log.d(TAG, "onCreate: started.");
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Tu solicitud");
        setSupportActionBar(tb);
        imgRequestPhoto = findViewById(R.id.imgRecommend);
        txtDescription = findViewById(R.id.txtRecommendDescpription);
        txtTitle = findViewById(R.id.txtRecommendTitle);
        txtLimitDate = findViewById(R.id.txtDate);
        getIncomingIntent();
        this.dB = FirebaseFirestore.getInstance();
    }

    private void initRecyclerView(ArrayList<HashMap<String, String>> answerList) {
        recyclerView = findViewById(R.id.rVAnswer);
        AnswersRecyclerViewAdapter adapter = new AnswersRecyclerViewAdapter(RequestRecommendActivity.this, answerList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");

        Intent intent = getIntent();
        if (intent.hasExtra("requestRecommendation")) {
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            RequestRecommendation requestRecommendation = (RequestRecommendation) intent.getSerializableExtra("requestRecommendation");
            txtDescription.setText(requestRecommendation.getDescription());
            txtTitle.setText(requestRecommendation.getTitle());
            txtLimitDate.setText(intent.getStringExtra("dateFormat"));
            setImage(requestRecommendation.getRequestPhoto());
            answers = requestRecommendation.getAnswers();
            requestId = requestRecommendation.getDocumentId();
            initRecyclerView(requestRecommendation.getAnswers());
        }
    }

    private void setImage(String imageUrl) {
        Log.d(TAG, "setImage: setting te image and name to widgets.");

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(imgRequestPhoto);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                dB.collection("requestRecommendations").document(requestId).update("answers",answers);

    }
}

