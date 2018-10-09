package com.mylook.mylook.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.mylook.mylook.R;

import java.util.HashMap;
import java.util.Map;

public class RecommendActivityAddDesc extends AppCompatActivity {

    private Button btnBack, btnSend;
    private ImageView imgRecommend;
    private TextInputEditText txtDescription;
    private EditText txtLimitDate;
    private Switch btnUbication;
    private FirebaseFirestore dB;
    private StorageReference storageRef;
    private Uri selectImageUri;
    private Bitmap bitmap;


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

        imgRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RecommendActivityAddImg.class);
                startActivity(intent);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToFirebase();
            }
        });
        if(!getIntent().equals(null)) {
            Intent intent = getIntent();
            if (intent.hasExtra("imgRecommend"))
                if (intent.getParcelableExtra("imgRecommend").getClass() == Bitmap.class) {
                    bitmap = intent.getParcelableExtra("imgRecommend");
                    imgRecommend.setImageBitmap(bitmap);
                } else {
                    selectImageUri = intent.getParcelableExtra("imgRecommend");
                    imgRecommend.setImageURI(selectImageUri);
                }
        }
        dB = FirebaseFirestore.getInstance();
     //   storageRef = FirebaseStorage.getInstance().getReference();
    }

    /*
    private Uri saveImage(String key){
        final Uri[] dowloadUrl = new Uri[1];
        StorageReference storageReference= storageRef.child("requestPhotos/"+key+".jpg");
        if(bitmap.equals(null)) {
            storageReference.putFile(selectImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dowloadUrl[0] =taskSnapshot.getDownloadUrl();
                }
            });
        }else{
            storageReference.putBytes(bitmap.getNinePatchChunk()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dowloadUrl[0] =taskSnapshot.getDownloadUrl();
                }
            });
        }
        return dowloadUrl[0];
    }
*/
    private void sendToFirebase()
    {
   //     Uri photoUri= saveImage("1");
        Map<String,Object> recommendation=new HashMap<>();
        recommendation.put("userName","user");
        recommendation.put("description",txtDescription.getText().toString());
        recommendation.put("limitDate",txtLimitDate.getText().toString());
        recommendation.put("updateDate","update");
        recommendation.put("requestPhoto","photoUri");
        recommendation.put("localization","location");
        recommendation.put("state",false);


        dB.collection("requestRecommendations")
                .add(recommendation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        }
                });

    }
}
