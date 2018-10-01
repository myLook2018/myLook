package com.mylook.mylook.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.mylook.mylook.R;

public class RecommendActivityAddDesc extends AppCompatActivity {

    private Button btnBack, btnSend;
    private ImageView imgRecommend;
    private TextInputEditText txtDescription;
    private EditText txtLimitDate;
    private Switch btnUbication;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_desc);
        btnBack=(Button) findViewById(R.id.btnBack);
        btnSend=(Button) findViewById(R.id.btnSend);
        imgRecommend = (ImageView) findViewById(R.id.imgRecommend);
        txtDescription=(TextInputEditText) findViewById(R.id.txtDescription);
        txtLimitDate=(EditText) findViewById(R.id.txtLimitDate);
        btnUbication=(Switch) findViewById(R.id.btnUbication);
        Intent intent = getIntent();
        Bitmap bitmap=intent.getParcelableExtra("imgRecommend"); //validarrrrrrrrr
        imgRecommend.setImageBitmap(bitmap);


    }
}
