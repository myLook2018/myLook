package com.mylook.mylook.recommend;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylook.mylook.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RecommendActivityAddDesc extends AppCompatActivity {

    private Button btnBack, btnSend;
    private ImageView imgRecommend = null;
    private TextInputEditText txtDescription;
    private Date limitDate;
    private EditText editDate;
    private Switch btnUbication;
    private FirebaseFirestore dB;
    private StorageReference storageRef;
    private Uri selectImageUri = null;
    private Bitmap bitmap = null;
    private boolean permissionGranted = true;
    private final int REQUEST_CAMERA = 1, SELECT_FILE = 0, READ_EXTERNAL_STORAGE = 1;
    protected LocationManager locationManager;
    protected LocationListener locationListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_desc);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        imgRecommend = (ImageView) findViewById(R.id.imgRecommend);
        txtDescription = (TextInputEditText) findViewById(R.id.txtDescription);
        editDate = (EditText) findViewById(R.id.editDate);
        btnUbication = (Switch) findViewById(R.id.btnUbication);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        imgRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecommendActivityAddImg.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 1);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToFirebase();
            }
        });
        if (!getIntent().equals(null)) {
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
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("Nueva Solicitud");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        storageRef = FirebaseStorage.getInstance().getReference();
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                limitDate = new Date();
                limitDate.setTime(myCalendar.getTimeInMillis());
                editDate.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RecommendActivityAddDesc.this, date , myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private Uri[] saveImage(String key) {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        final StorageReference storageReference = storageRef.child("requestPhotos/" + this.createPhotoName("userName") + ".jpg");
        Uri[] downloadUrl = {Uri.parse(storageReference.getPath())};
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                permissionGranted= true;
            }
        }
        if(permissionGranted) {
            if (bitmap == null) {
                storageReference.putFile(selectImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayMessage("Fue cargada con exito");
                    }
                });
            } else {
                storageReference.putBytes(bitmap.getNinePatchChunk()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayMessage("Fue cargada con exito");
                    }
                });
            }
            return downloadUrl;
        } else {
            displayMessage("The app needs permission to use your media files");
            return null;
        }
    }

    private void sendToFirebase() {
        if (txtDescription.getText().toString().isEmpty()) {
            displayMessage("Please write a description");
            return;
        }
        Calendar cal = Calendar.getInstance();

        long days = TimeUnit.MILLISECONDS.toDays(limitDate.getTime() - cal.getTime().getTime());
        if ( days < 3) {
            displayMessage("Please pick a correct limit date, 3 days minimum");
            return;
        }
        if (selectImageUri == null) {
            displayMessage("Please select an image");
            return;
        }
        Uri[] photoUri = saveImage("1");
        Location loc = getLocation();
        if(photoUri.length>0&& photoUri[0] != null && loc!=null) {
            final Map<String, Object> recommendation = new HashMap<>();
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            recommendation.put("userId", user.getUid());
            recommendation.put("userName", user.getDisplayName());
            recommendation.put("description", txtDescription.getText().toString());
            recommendation.put("limitDate", cal.getTimeInMillis());
            recommendation.put("updateDate", "update");
            recommendation.put("requestPhoto", photoUri[0].toString());
            recommendation.put("localization", loc);
            recommendation.put("state", false);
            dB.collection("requestRecommendations")
                    .add(recommendation)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            displayMessage("Your recommendation has been sent");
                            //finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            displayMessage("There has been a problem while uploading");
                        }
                    });
        } else {
            displayMessage("Error en la creacion del array");
        }

    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String createPhotoName(String userName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD_MM_HH_mm_ss");
        String photoName = userName + "_" + sdf.format(cal.getTime());
        System.out.println(photoName);
        return photoName;
    }

    private Location getLocation(){
        boolean locPermission = true;
        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                locPermission = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                locPermission= true;
            }
        }

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if ((boolean) data.getExtras().get("isBitmap")) {
                bitmap = (Bitmap) data.getExtras().get("imgRecommend");
                imgRecommend.setImageBitmap(bitmap);
            } else {
                selectImageUri = (Uri) data.getExtras().get("imgRecommend");
                imgRecommend.setImageURI(selectImageUri);
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
                    permissionGranted = true;
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    }




