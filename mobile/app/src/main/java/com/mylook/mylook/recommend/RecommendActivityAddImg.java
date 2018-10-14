package com.mylook.mylook.recommend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mylook.mylook.R;

public class RecommendActivityAddImg extends AppCompatActivity {

    private static final int ACTIVITY_NUM = 2,REQUEST_CAMERA = 1;
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

    private Integer SELECT_FILE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_img);

        btnPhoto = (ImageView) findViewById(R.id.btnCamera);
        btnGallery = (ImageView) findViewById(R.id.btnGallery);
        btnCloset = (ImageView) findViewById(R.id.btnCloset);
        btnNext = (Button) findViewById(R.id.btnNext);
        cameraView = (ImageView) findViewById(R.id.cameraView);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCameraAccess()) {
                    try {
                        startCameraIntent();
                    } catch (Exception e){

                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecommendActivityAddDesc.class);
                if (banImage) {
                    intent.putExtra("imgRecommend", bitmap);
                    intent.putExtra("isBitmap", true);
                } else {
                    intent.putExtra("imgRecommend", selectImageUri);
                    intent.putExtra("isBitmap", false);
                }
                setResult(Activity.RESULT_OK, intent);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                finish();

            }
        });
    }

    private boolean getCameraAccess() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return true;
        }
        return false;
    }

    private void startCameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                cameraView.setImageBitmap(bitmap);
                banImage = true;
            } else if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                cameraView.setImageURI(selectImageUri);
                banImage = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startCameraIntent();
                } else {
                }
                break;
            }


        }
    }
}
