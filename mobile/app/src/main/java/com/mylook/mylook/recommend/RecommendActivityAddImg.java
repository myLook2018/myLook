package com.mylook.mylook.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mylook.mylook.R;

public class RecommendActivityAddImg extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2;
    private Context mContext = RecommendActivityAddImg.this;

    private ImageView btnPhoto;
    private ImageView btnGallery;
    private ImageView btnCloset;
    private ImageView cameraView;
    private Button btnBack;
    private Button btnNext;
    private Bitmap bitmap;
    private Uri selectImageUri;
    private boolean banImage;

    private Integer REQUEST_CAMERA=1, SELECT_FILE=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_img);

        btnPhoto=(ImageView) findViewById(R.id.btnCamera);
        btnGallery=(ImageView) findViewById(R.id.btnGallery);
        btnCloset=(ImageView) findViewById(R.id.btnCloset);
        btnNext=(Button) findViewById(R.id.btnNext);
        cameraView =(ImageView) findViewById(R.id.cameraView);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAMERA);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RecommendActivityAddDesc.class);
                if(banImage)
                    intent.putExtra("imgRecommend",bitmap);
                else
                    intent.putExtra("imgRecommend",selectImageUri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUEST_CAMERA){
                bitmap=(Bitmap) data.getExtras().get("data");
                cameraView.setImageBitmap(bitmap);
                banImage=true;
            }else if(requestCode==SELECT_FILE){
                selectImageUri=data.getData();
                cameraView.setImageURI(selectImageUri);
                banImage=false;
            }
        }

    }
}