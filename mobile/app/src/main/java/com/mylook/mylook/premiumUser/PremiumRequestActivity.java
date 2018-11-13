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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.firestore.SetOptions;
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

public class PremiumRequestActivity extends AppCompatActivity {

    private final int SELECT_FILE = 0;
    private TextView txtProfilePhoto;
    private ImageView imgProfilePhoto;
    private AutoCompleteTextView txtEmail, txtLocalization;
    private AutoCompleteTextView txtIg, txtFacebook;
    private Button btnRequest;
    private Uri selectImageUri = null;
    private StorageReference storageRef;
    private FirebaseUser user;
    private boolean permissionGranted = true;
    private Uri downloadUrl;
    private ProgressBar mProgressBar;
    private boolean enviado = false;
    private Timestamp premiumDate;
    private String clientId;
    private FirebaseFirestore dB;
    private String userName;
    private final int READ_EXTERNAL_STORAGE = 1, WRITE_EXTERNAL_STORAGE = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_premium_account);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Cuenta Destacada");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        dB = FirebaseFirestore.getInstance();
        clientId = getIntent().getStringExtra("clientId");
        userName = getIntent().getStringExtra("userName");

        initElements();
        setOnclicks();
    }

    private void setOnclicks() {
        txtProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
        btnRequest.setOnClickListener(new View.OnClickListener() {
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
            checkWrite(resultCode, data);
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
            imgProfilePhoto.setImageBitmap(null);
            Glide.with(this).asBitmap().load(selectedImage).into(imgProfilePhoto);
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
        final StorageReference storageReference = storageRef.child("profilePremium/" + this.createPhotoName(user.getDisplayName()) + ".jpg");
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
        if (txtEmail.getText().toString().isEmpty()) {
            displayMessage("Debe añadir un Mail de contacto!");
            return;
        }
        if (txtLocalization.getText().toString().isEmpty()) {
            displayMessage("Debe añadir una Localidad!");
            return;
        }
        if (txtIg.getText().toString().isEmpty()) {
            displayMessage("Debe añadir tu Instagram");
            return;
        }

        if (selectImageUri == null) {
            displayMessage("Debe añadir una foto de perfil!");
            return;
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            btnRequest.setEnabled(false);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setImage(selectImageUri);
                }
                break;
            }
            case WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    setImage(selectImageUri);
                }
                break;
            }
        }
    }

    private boolean writeFirebaseDocument(String uri) {
        if (!enviado) {
            mProgressBar.setVisibility(View.VISIBLE);
            btnRequest.setEnabled(false);
            setCurrentDate();
            final Map<String, Object> premiumUser = new HashMap<>();
            premiumUser.put("userId", user.getUid());
            premiumUser.put("profilePhoto", uri);
            premiumUser.put("localization", txtLocalization.getText().toString());
            premiumUser.put("premiumDate", premiumDate);
            premiumUser.put("contactMail", txtEmail.getText().toString());
            premiumUser.put("linkInstagram", txtIg.getText().toString());
            premiumUser.put("linkFacebook", txtFacebook.getText().toString());
            premiumUser.put("clientId", clientId);
            premiumUser.put("userName", userName);

            dB.collection("premiumUsers")
                    .add(premiumUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Map<String, Object> client = new HashMap<>();
                    client.put("isPremium", true);
                    dB.collection("clients").document(clientId).set(client, SetOptions.mergeFields("isPremium")).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            enviado = true;
                            mProgressBar.setVisibility(View.GONE);
                            displayMessage("Ya eres un usuario destacado");
                            finish();
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                    btnRequest.setEnabled(true);
                    displayMessage("Ha ocurrido un problema con tu solicitud");
                }
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            btnRequest.setEnabled(true);
            Log.e("WRITE FIREBASE", "no se por que entra aca, ahrrrre");
        }
        return enviado;
    }

    private void setCurrentDate() {
        Calendar cal = Calendar.getInstance();
        this.premiumDate = new Timestamp(cal.getTime());
    }


    private void initElements() {

        txtProfilePhoto = findViewById(R.id.txtProfilePhoto);
        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);
        txtEmail = findViewById(R.id.txtEmail);
        txtLocalization = findViewById(R.id.txtLocalization);
        txtIg = findViewById(R.id.txtIg);
        txtFacebook = findViewById(R.id.txtFacebook);
        btnRequest = findViewById(R.id.btnRequest);
        mProgressBar = findViewById(R.id.progressBar);
    }
}
