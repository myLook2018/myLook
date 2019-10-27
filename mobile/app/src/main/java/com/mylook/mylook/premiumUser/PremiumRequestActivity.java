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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.PremiumUser;
import com.mylook.mylook.session.Session;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PremiumRequestActivity extends AppCompatActivity {

    private static final int SUCCESS_CODE = 0;
    private static final int UNSUCCESS_CODE = 2;
    private final int SELECT_FILE = 0;
    private TextView txtProfilePhoto;
    private CircleImageView imgProfilePhoto;
    private AutoCompleteTextView txtEmail, txtLocalization;
    private AutoCompleteTextView txtIg, txtFacebook;
    private ImageButton btnRequest;
    private Uri selectImageUri = null;
    private FirebaseUser user;
    private boolean permissionGranted = true;
    private Uri downloadUrl;
    private ProgressBar mProgressBar;
    private boolean enviado = false;
    private Timestamp premiumDate;
    private String clientId;
    private String userName;
    private final int READ_EXTERNAL_STORAGE = 1, WRITE_EXTERNAL_STORAGE = 2;
    private boolean isChange=false;
    private PremiumUser premiumUser;
    private String documentPath;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_premium_account);
        Toolbar tb = findViewById(R.id.toolbar);
        initElements();
        user = FirebaseAuth.getInstance().getCurrentUser();
        clientId = getIntent().getStringExtra("clientId");
        userName = getIntent().getStringExtra("userName");
        if(getIntent().hasExtra("isChange")){
            isChange= getIntent().getBooleanExtra("isChange",true);
            tb.setTitle("Edita tus datos publicos");
            setPremiumUserData();
        }else{
            tb.setTitle("Cuenta Destacada");
        }
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setOnclicks();
    }

    private void setPremiumUserData() {
        FirebaseFirestore.getInstance().collection("premiumUsers").whereEqualTo("clientId",clientId)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                        premiumUser = queryDocumentSnapshots.getDocuments().get(0).toObject(PremiumUser.class);
                        txtEmail.setText(premiumUser.getContactMail());
                        txtFacebook.setText(premiumUser.getLinkFacebook());
                        txtIg.setText(premiumUser.getLinkInstagram());
                        txtLocalization.setText(premiumUser.getLocalization());
                        Glide.with(getApplicationContext()).load(premiumUser.getProfilePhoto()).into(this.imgProfilePhoto);
                        documentPath=queryDocumentSnapshots.getDocuments().get(0).getId();
                    }
                });
    }

    private void setOnclicks() {
        txtProfilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_FILE);
        });
        imgProfilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_FILE);
        });
        btnRequest.setOnClickListener(v -> sendRequest());
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
        if (resultCode == RESULT_OK) {
            selectImageUri = result.getUri();
           int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                setImage(selectImageUri);
            }

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
        final StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child("profilePremium/" + this.createPhotoName(user.getDisplayName()) + ".jpg");
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
            uploadTask.onSuccessTask((SuccessContinuation<UploadTask.TaskSnapshot, Object>) taskSnapshot -> {
                uploadTask.getSnapshot().getStorage().getDownloadUrl().onSuccessTask((SuccessContinuation<Uri, Object>) uri -> {
                    downloadUrl = uri;
                    return (Task) uploadTask;
                });
                return (Task) uploadTask;
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

        if(isChange==false){

            if (selectImageUri == null) {
                displayMessage("Debe añadir una foto de perfil!");
                return;
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                btnRequest.setEnabled(false);
                final UploadTask uptask = saveImage();
                assert uptask != null;
                uptask.addOnCompleteListener(task ->
                        Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                    writeFirebaseDocument(task1.getResult().toString());
                                }));
            }
        }else{
            if (selectImageUri != null) {
                mProgressBar.setVisibility(View.VISIBLE);
                btnRequest.setEnabled(false);
                final UploadTask uptask = saveImage();
                assert uptask != null;
                uptask.addOnCompleteListener(task ->
                        Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl()
                                .addOnCompleteListener(task1 -> {
                                    updateFirebaseDocument(task1.getResult().toString());
                                }));
            }else{
                updateFirebaseDocument(premiumUser.getProfilePhoto());
            }
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
    private boolean updateFirebaseDocument(String uri){

        if (!enviado) {
            mProgressBar.setVisibility(View.VISIBLE);
            btnRequest.setEnabled(false);
            setCurrentDate();
            final Map<String, Object> premiumUser = new HashMap<>();
            premiumUser.put("profilePhoto", uri);
            premiumUser.put("localization", txtLocalization.getText().toString());
            premiumUser.put("contactMail", txtEmail.getText().toString());
            premiumUser.put("linkInstagram", txtIg.getText().toString());
            premiumUser.put("linkFacebook", txtFacebook.getText().toString());


            FirebaseFirestore.getInstance().collection("premiumUsers").document(documentPath).update(premiumUser)
                    .addOnSuccessListener(aVoid -> {
                        enviado = true;
                        mProgressBar.setVisibility(View.GONE);
                        displayMessage("Datos Actualizados");
                        finish();
                    })
                .addOnFailureListener(e -> {
                mProgressBar.setVisibility(View.GONE);
                btnRequest.setEnabled(true);
                displayMessage("Ha ocurrido un problema con tu solicitud");
            });
        } else {
            mProgressBar.setVisibility(View.GONE);
            btnRequest.setEnabled(true);
            Log.e("WRITE FIREBASE", "no se por que entra aca, ahrrrre");
        }
        return enviado;
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

            FirebaseFirestore.getInstance().collection("premiumUsers")
                    .add(premiumUser).addOnSuccessListener(documentReference -> {
                        Map<String, Object> client = new HashMap<>();
                        client.put("isPremium", true);
                        FirebaseFirestore.getInstance().collection("clients").document(clientId).set(client, SetOptions.mergeFields("isPremium")).addOnCompleteListener(task -> {
                            enviado = true;
                            mProgressBar.setVisibility(View.GONE);
                            displayMessage("Ya eres un usuario destacado");
                            setResult(SUCCESS_CODE);
                            Session.updateData();
                            Session.setIsPremium(true);
                            finish();
                        });


                    }).addOnFailureListener(e -> {
                        mProgressBar.setVisibility(View.GONE);
                        btnRequest.setEnabled(true);
                        displayMessage("Ha ocurrido un problema con tu solicitud");
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

    @Override
    public void onBackPressed() {
        setResult(UNSUCCESS_CODE);
        this.finish();
    }
}
