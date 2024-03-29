package com.mylook.mylook.recommend;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mylook.mylook.R;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.session.Session;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class RecommendActivityAddDesc extends AppCompatActivity {

    private ImageButton btnSend;
    private ImageView imgRecommend = null;
    private TextInputEditText txtDescription;
    private Date limitDate;
    private EditText editDate;
    private StorageReference storageRef;
    private TextView title;
    private Uri selectImageUri = null;
    private Bitmap bitmap = null;
    private Uri picUri;
    private TextInputEditText txtSize;
    private boolean permissionGranted = true;
    private final int REQUEST_CAMERA = 3, SELECT_FILE = 0, READ_EXTERNAL_STORAGE = 1, LOCATION_PERMISSION = 2, PIC_CROP = 4, WRITE_EXTERNAL_STORAGE = 5;
    //keep track of cropping intent
    protected LocationManager locationManager;
    private Location currentLocation;
    private FloatingActionButton fabImage;
    private FirebaseUser user;
    private String urlLogo = "https://firebasestorage.googleapis.com/v0/b/mylook-develop.appspot.com/o/utils%2Flogo_transparente_50.png?alt=media&token=c72e5b39-3011-4f26-ba4f-4c9f7326c68a";
    private ProgressBar mProgressBar;
    private MaterialBetterSpinner spinner;
    private Uri downloadUrl;
    private boolean enviado = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_recommendation_add_desc);

        invalidateOptionsMenu();
        initElements();
        Toolbar tb = findViewById(R.id.recomend_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Nueva Solicitud");
        setCurrentLocation();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sendToFirebase();
                }catch (Exception e)
                {
                    displayMessage("Algo salio mal :(");
                    Log.e("Recommendations exc ->", e.getMessage());
                }
            }
        });

        fabImage.setOnClickListener(l -> {
            CropImage.activity().
                    setAspectRatio(1,1).
                    setGuidelines(CropImageView.Guidelines.ON)

                    .setActivityTitle("Tu imagen").
                    start(this);
        });
        setCategoryRequest();
        initCalendar();

    }


    private void initCalendar() {

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                limitDate = new Date();
                limitDate.setTime(myCalendar.getTimeInMillis());
                editDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datepick  = new DatePickerDialog(RecommendActivityAddDesc.this,R.style.CalendarTheme,date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datepick.show();//
                //No saben lo que me costó encontrar estos métodos
                datepick.getButton(DatePickerDialog.BUTTON_NEGATIVE).getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                datepick.getButton(DatePickerDialog.BUTTON_NEUTRAL).getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                datepick.getButton(DatePickerDialog.BUTTON_POSITIVE).getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                //Estos fueron más fáciles
                datepick.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_dark));
                datepick.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_dark));
                datepick.getButton(DatePickerDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.primary_dark));

            }
        });
    }

    private void initElements() {
        spinner = findViewById(R.id.category);
        txtSize = findViewById(R.id.size_input);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        mProgressBar = findViewById(R.id.progressBar);
        btnSend = findViewById(R.id.btnSend);
        imgRecommend = (ImageView) findViewById(R.id.imgRecommend);
        txtDescription = (TextInputEditText) findViewById(R.id.txtDescription);
        editDate = (EditText) findViewById(R.id.editDate);
        title = findViewById(R.id.txtTitle);
        fabImage = findViewById((R.id.fab_add_image));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Intent intent = getIntent();

    }

    private void setCategoryRequest() {
        FirebaseFirestore.getInstance().collection("categories").whereEqualTo("name", "recommendation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> categories = (ArrayList<String>) task.getResult().getDocuments().get(0).get("categories");
                spinner.setAdapter(new ArrayAdapter<String>(RecommendActivityAddDesc.this, android.R.layout.simple_selectable_list_item, categories));
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
        //use standard intent to capture an image
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //we will handle the returned data in onActivityResult
        startActivityForResult(captureIntent, REQUEST_CAMERA);

    }

    private UploadTask saveImage() {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        final StorageReference storageReference = storageRef.child("requestPhotos/" + this.createPhotoName(user.getDisplayName()) + ".jpg");
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
            if (bitmap == null) {
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                uploadTask = storageReference.putBytes(data);
                uploadTask.onSuccessTask(new SuccessContinuation<UploadTask.TaskSnapshot, Object>() {
                    @NonNull
                    @Override
                    public Task<Object> then(@Nullable UploadTask.TaskSnapshot taskSnapshot) {
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

            }
        } else {
            displayMessage("The app needs permission to use your media files");
            return null;
        }
        return uploadTask;
    }

    private boolean writeFirebaseDocument(String uri) {
        if (!enviado) {
            Session.getInstance().updateActivitiesStatus(Session.RECOMEND_FRAGMENT);
            mProgressBar.setVisibility(View.VISIBLE);
            btnSend.setEnabled(false);
            fabImage.hide();
//            fabImage.setVisibility(View.INVISIBLE);
            setCurrentLocation();
            if (currentLocation != null) {
                final List<Double> latLong = new Vector<>();
                latLong.add(currentLocation.getLatitude());
                latLong.add(currentLocation.getLongitude());
                final Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("userId", user.getUid());
                recommendation.put("description", txtDescription.getText().toString());
                recommendation.put("limitDate", limitDate.getTime());
                recommendation.put("updateDate", "update");
                recommendation.put("requestPhoto", uri);
                recommendation.put("localization", latLong);
                recommendation.put("isClosed", false);
                recommendation.put("title", title.getText().toString());
                recommendation.put("answers", new ArrayList<ArrayList<String>>());
                recommendation.put("category", spinner.getText().toString());
                if (!txtSize.getText().equals(""))
                    recommendation.put("size", txtSize.getText().toString());
                FirebaseFirestore.getInstance().collection("requestRecommendations")
                        .add(recommendation).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        enviado = true;
                        mProgressBar.setVisibility(View.GONE);
                        displayMessage("Tu solicitud de recomendación ha sido enviada");
                        setResult(1);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        btnSend.setEnabled(true);
                        fabImage.show();

                        displayMessage("Ha ocurrido un problema con tu recomendacion");
                    }
                });
            } else {
                mProgressBar.setVisibility(View.GONE);
                btnSend.setEnabled(true);
                fabImage.show();
                showLocationAlert();
            }
        }
        return enviado;
    }

    private void sendToFirebase() {
        Calendar cal = Calendar.getInstance();
        if (title.getText().toString().isEmpty()) {
            displayMessage("Debe añadir un titulo!");
            return;
        }
        if (txtDescription.getText().toString().isEmpty()) {
            displayMessage("Debe añadir una descripción!");
            return;
        }
        if (limitDate == null) {
            displayMessage("Debe ingresar una fecha!");
            return;
        } else {
            long days = TimeUnit.MILLISECONDS.toDays(limitDate.getTime() - cal.getTime().getTime());
            if (days < 7) {
                displayMessage("La fecha limite debe ser mayor a 7 días!");
                return;
            }
        }
        if (spinner.getText().equals("")) {
            displayMessage("Seleccioná una categoría");
        }


        if (bitmap == null && selectImageUri == null) {
            writeFirebaseDocument(urlLogo);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            btnSend.setEnabled(false);
            fabImage.hide();
            final UploadTask uptask = saveImage();
            uptask.addOnCompleteListener(task ->
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                        writeFirebaseDocument(task1.getResult().toString());
                    }));
        }

    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

        }

    }

    private void setCurrentLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
        }
        if (isLocationEnabled() && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new MyLocationListenerGPS(), null);
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (currentLocation == null) {
                    locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, new MyLocationListenerGPS(), null);
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (currentLocation == null) {
                        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new MyLocationListenerGPS(), null);
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private AlertDialog showLocationAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        final AlertDialog alert = dialog.setTitle("Activar Ubicación")
                .setMessage("Tu ubicación esta desactivada..\nDebes activarla para continuar")
                .setPositiveButton("Ajustes de localización", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }

                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                }).create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.purple));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.purple));
            }
        });

        alert.show();
        return alert;
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
        Log.e("Request code", ""+requestCode);
        Log.e("Result code", ""+resultCode);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectImageUri = result.getUri();
                imgRecommend.setImageURI(selectImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void perfromCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)

                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                //indicate image type and Uri
                cropIntent.setDataAndType(picUri, "image/*");
                //set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                //retrieve data on return
                cropIntent.putExtra("return-data", true);
                //start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        onBackPressed();
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                }
                break;
            }
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    btnSend.setEnabled(true);
                    fabImage.show();
                } else {
                    setCurrentLocation();
                }
                break;
            }
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraIntent();
                }
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}




