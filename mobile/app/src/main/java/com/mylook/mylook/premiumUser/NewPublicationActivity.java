package com.mylook.mylook.premiumUser;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylook.mylook.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewPublicationActivity extends AppCompatActivity {
    private final int  SELECT_FILE = 0;
    private TextView txtPublicationPhoto;
    private ImageView imgPublicationPhoto;
    private AutoCompleteTextView txtStoreName, txtArticleCode;
    private Button btnSave;
    private Uri selectImageUri = null;
    private StorageReference storageRef;
    private FirebaseUser user;
    private boolean permissionGranted = true;
    private Uri downloadUrl;
    private ProgressBar mProgressBar;
    private boolean enviado = false;
    private FirebaseFirestore dB;
    private String clientId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_publication);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Tu publicación");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        user= FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        dB=FirebaseFirestore.getInstance();
        clientId=getIntent().getStringExtra("clientId");
        initElements();
        setOnclicks();
    }

    private void setOnclicks() {
        txtPublicationPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
        imgPublicationPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }

    private void sendRequest() {
        sendToFirebase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                CropImage.activity(selectImageUri)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            checkWrite(resultCode,data);
        }
    }
    private void checkWrite(int resultCode, Intent data){
        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            cropActivity(resultCode, data);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                cropActivity(resultCode, data);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                cropActivity(resultCode, data);
            }
        }

    }

    private void cropActivity(int resultCode, Intent data){
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        selectImageUri = result.getUri();
        if (resultCode == RESULT_OK) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                setImage(selectImageUri);
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    setImage(selectImageUri);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    setImage(selectImageUri);
                }
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
        }
    }
    private void setImage(Uri uri) {
        try{
            final InputStream imageStream = getContentResolver().openInputStream(uri);

            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imgPublicationPhoto.setImageBitmap(null);
            Glide.with(this).asBitmap().load(selectedImage).into(imgPublicationPhoto);
        }catch (Exception e){
            Log.e("Set image", e.getMessage());
        }
//        selectImageUri = uri;
//        imgProfilePhoto.setImageURI(selectImageUri);
    }
    private String createPhotoName(String userName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD_MM_HH_mm_ss");
        String photoName = userName + "_profile_" + sdf.format(cal.getTime());
        System.out.println(photoName);
        return photoName;
    }
    private UploadTask saveImage() {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        final StorageReference storageReference = storageRef.child("premiumPublications/" + this.createPhotoName(user.getDisplayName()) + ".jpg");
        final UploadTask uploadTask;
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                permissionGranted = true;
            }
        }
        if (permissionGranted) {
            uploadTask = storageReference.putFile(selectImageUri);
            uploadTask.onSuccessTask(new SuccessContinuation<UploadTask.TaskSnapshot, Object>() {
                @NonNull
                @Override
                public Task<Object> then(@Nullable final UploadTask.TaskSnapshot taskSnapshot) {
                    uploadTask.getSnapshot().getStorage().getDownloadUrl().onSuccessTask(new SuccessContinuation<Uri, Object>() {
                        @NonNull
                        @Override
                        public Task<Object> then(@Nullable Uri uri) {
                            downloadUrl = uri;
                            return (Task) uploadTask;
                        }
                    });
                    return (Task) uploadTask;
                }
            });

        } else {
            displayMessage("The app needs permission to use your media files");
            return null;
        }
        return uploadTask;
    }
    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void sendToFirebase() {
        Calendar cal = Calendar.getInstance();
        if (txtStoreName.getText().toString().isEmpty()) {
            displayMessage("Debe añadir una Tienda");
            return;
        }else
        {
            Task<QuerySnapshot> task= validateStore(txtStoreName.getText().toString());
            if(task.isComplete()){
                if (txtArticleCode.getText().toString().isEmpty()) {
                    displayMessage("Debe añadir un código para el artículo");
                    return;
                }else
                {
                    Task<QuerySnapshot> task2=validateCode(txtArticleCode.getText().toString(),txtStoreName.getText().toString());
                }
            }

        }

        if ( selectImageUri == null) {
            displayMessage("Debe añadir una foto a la publicación");
            return;
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            final UploadTask uptask = saveImage();
            uptask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            writeFirebaseDocument(task.getResult().toString());
                        }
                    });


                }
            });
        }

    }
    private Task<QuerySnapshot> validateStore(String store){
        Task <QuerySnapshot> task = dB.collection("stores").whereEqualTo("storeName",store).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().getDocuments().isEmpty()){
                                displayMessage("No existe tienda");
                            return;
                            }
                            else{
                                return;
                            }
                        }
                    }
                });

        return task;
    }
    private Task<QuerySnapshot> validateCode(String code,String store){
        Task<QuerySnapshot> task=dB.collection("articles").whereEqualTo("storeName",store).whereEqualTo("code",code).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult().getDocuments().isEmpty()){
                                displayMessage("No existe Articulo en esa tienda");
                                return;
                            }
                            else{
                                return;
                            }
                        }
                    }
                });
        return task;
    }
    private boolean writeFirebaseDocument(String uri) {
        if (!enviado) {
            Calendar cal = Calendar.getInstance();
            new Timestamp(cal.getTime());
            mProgressBar.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            final Map<String, Object> premiumPublication = new HashMap<>();
            premiumPublication.put("storeName", txtStoreName.getText().toString());
            premiumPublication.put("publicationPhoto", uri);
            premiumPublication.put("articleCode", txtArticleCode.getText().toString());
            premiumPublication.put("userId",user.getUid());
            premiumPublication.put("clientId",clientId);
            premiumPublication.put("creationDate",new Timestamp(cal.getTime()));

            dB.collection("premiumPublications")
                    .add(premiumPublication).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    displayMessage("Tu publicación fue guardada");
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    displayMessage("Ha ocurrido un problema con tu publicación");
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            Log.e("WRITE FIREBASE","no se por que entra aca, ahrrrre");
        }
        return enviado;
    }


    private void initElements() {
        txtPublicationPhoto=findViewById(R.id.txtPublicationPhoto);
        imgPublicationPhoto =findViewById(R.id.imgPublication);
        txtStoreName =findViewById(R.id.txtStoreName);
        txtArticleCode =findViewById(R.id.txtArticleCode);
        btnSave =findViewById(R.id.btnSave);
        mProgressBar=findViewById(R.id.progressBar);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
