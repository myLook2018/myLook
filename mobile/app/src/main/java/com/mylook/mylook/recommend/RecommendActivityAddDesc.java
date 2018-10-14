package com.mylook.mylook.recommend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class RecommendActivityAddDesc extends AppCompatActivity {

    private Button btnBack, btnSend;
    private ImageView imgRecommend = null;
    private TextInputEditText txtDescription;
    private Date limitDate;
    private EditText editDate, title;
    private FirebaseFirestore dB;
    private StorageReference storageRef;
    private Uri selectImageUri = null;
    private Bitmap bitmap = null;
    private boolean permissionGranted = true;
    private final int REQUEST_CAMERA = 3, SELECT_FILE = 0, READ_EXTERNAL_STORAGE = 1, LOCATION_PERMISSION = 2;
    protected LocationManager locationManager;
    private Location currentLocation;
    private FloatingActionMenu fabMenu;
    private FloatingActionButton fabPhoto, fabGallery;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_desc);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSend = (Button) findViewById(R.id.btnSend);
        imgRecommend = (ImageView) findViewById(R.id.imgRecommend);
        txtDescription = (TextInputEditText) findViewById(R.id.txtDescription);
        editDate = (EditText) findViewById(R.id.editDate);
        title = (EditText) findViewById(R.id.title);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fabPhoto = (FloatingActionButton) findViewById(R.id.photoFloating);
        fabGallery = (FloatingActionButton) findViewById(R.id.galleryFloating);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToFirebase();
            }
        });
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
                editDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RecommendActivityAddDesc.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCameraAccess()) {
                    try {
                        startCameraIntent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });
    }


    private boolean getCameraAccess() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return true;
        } else {
            return true;
        }
    }

    private void startCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    private Uri[] saveImage(String key) {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference storageReference = storageRef.child("requestPhotos/" + this.createPhotoName(user.getDisplayName()) + ".jpg");
        Uri[] downloadUrl = {Uri.parse(storageReference.getPath().toString())};
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
            if (bitmap == null) {
                storageReference.putFile(selectImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
        if (days < 3) {
            displayMessage("Please pick a correct limit date, 3 days minimum");
            return;
        }
        if (selectImageUri == null && imgRecommend ==null) {
            displayMessage("Please select an image");
            return;
        }
        if (title.getText().toString().isEmpty()) {
            displayMessage("Please write a title");
            return;
        }
        Location loc = getLocation();
        FirebaseUser user = null;
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
        } catch (Exception e) {
            System.out.println(e);
        }

        if (loc != null && user != null) {
            List<Double> latLong = new Vector<>();
            latLong.add(loc.getLatitude());
            latLong.add(loc.getLongitude());
            final Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("userId", user.getUid());
            recommendation.put("userName", user.getDisplayName());
            recommendation.put("description", txtDescription.getText().toString());
            recommendation.put("limitDate", cal.getTimeInMillis());
            recommendation.put("updateDate", "update");
            recommendation.put("requestPhoto", saveImage("1")[0].toString());
            recommendation.put("localization", latLong);
            recommendation.put("state", false);
            recommendation.put("title", title.getText().toString());
            dB.collection("requestRecommendations")
                    .add(recommendation)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            displayMessage("Your recommendation has been sent");
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            displayMessage("There has been a problem while uploading");
                        }
                    });
        } else {
            displayMessage("Error en la creacion de datos");
        }

    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private Location getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled()) {
            showLocationAlert();
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }

        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new MyLocationListenerGPS(), null);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation != null) {
                return currentLocation;
            }
            locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, new MyLocationListenerGPS(), null);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (currentLocation != null) {
                return currentLocation;
            }

            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListenerGPS(), null);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return currentLocation;
    }

    public class MyLocationListenerGPS implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            showLocationAlert();
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showLocationAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    private String createPhotoName(String userName) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("DD_MM_HH_mm_ss");
        String photoName = userName + "_" + sdf.format(cal.getTime());
        System.out.println(photoName);
        return photoName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imgRecommend.setBackgroundColor(Color.rgb(255, 255, 255));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imgRecommend.setImageBitmap(bitmap);
                fabMenu.close(true);
            } else if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                imgRecommend.setImageURI(selectImageUri);
                fabMenu.close(true);
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
                break;
            }
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showLocationAlert();
                }
                break;
            }
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




