package com.mylook.mylook.login;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.User;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName, txtSurname, txtBirthdate;
    private Toolbar tb;
    private MaterialBetterSpinner spinner;
    private ImageButton btnRegister;
    private FirebaseUser user;
    final Calendar myCalendar = Calendar.getInstance();
    private String provider = null;
    private boolean validMail = true;
    private ArrayAdapter<String> adapterSex;
    private String LOG_LABEL = "[REGISTER]";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_LABEL, "On Create");
        setContentView(R.layout.activity_register);
        initElements();
        setupFirebaseAuth();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        getExtras();
        initCalendar();
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkExistingEmail(txtEmail.getText().toString());
                } else {
                    txtEmail.setTextColor(getResources().getColor(R.color.black));
                    validMail = true;
                }
            }
        });
        if (provider != null)
            disableFields();

    }

    private void checkExistingEmail(String email) {
        btnRegister.setEnabled(false);
        FirebaseFirestore.getInstance().collection("clients").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e(LOG_LABEL, "Check Existing Emmail, mail en firebase" + task.getResult().getDocuments().toString());
                        if (task.getResult().getDocuments().size() > 0) {
                            validMail = false;
                            txtEmail.setTextColor(getResources().getColor(R.color.red));
                            displayMessage("El mail ya está en uso");
                            btnRegister.setEnabled(false);
                        } else {
                            btnRegister.setEnabled(true);
                        }
                        mProgressBar.setVisibility(View.GONE);

                    }
                });
    }

    private void disableFields() {
        txtPasswd1.setEnabled(false);
        txtPasswd1.setText("****************");
        txtPasswd2.setEnabled(false);
        txtPasswd2.setText("****************");
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private boolean validateFields() {
        if (isStringNull(txtName.getText().toString())) {
            displayMessage("El campo Nombre es obligatorio");
            return false;
        }
        if (isStringNull(txtSurname.getText().toString())) {
            displayMessage("El campo Apellido es obligatorio");
            return false;
        }
        if (isStringNull(txtEmail.getText().toString())) {
            displayMessage("El campo Email es obligatorio");
            return false;
        }
        if (!validMail) {
            displayMessage("El Mail seleccionado ya está en uso");
            return false;
        }
        if (isStringNull(txtDNI.getText().toString())) {
            displayMessage("Debes ingresar un DNI");
            return false;
        }
        if (isStringNull(spinner.getText().toString())) {
            displayMessage("El campo Sexo es obligatorio");
            return false;
        }
        if (isStringNull(txtBirthdate.getText().toString())) {
            displayMessage("Debes ingresar una Fecha de Nacimiento");
            return false;
        }
        Calendar today = Calendar.getInstance();
        int diff = today.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
        if (diff < 14) {
            displayMessage("Debes tener al menos 14 años");
            return false;
        }
        if (isStringNull(txtPasswd1.getText().toString()) || isStringNull(provider)) {
            displayMessage("Debes ingresar una contraseña");
            return false;
        }
        if (txtPasswd1.getText().length() < 6 || isStringNull(provider)) {
            displayMessage("La contraseña debe contener 6 caracteres");
            txtPasswd1.setText("");
            txtPasswd2.setText("");
            return false;
        }
        if (isStringNull(txtPasswd2.getText().toString()) || isStringNull(provider)) {
            displayMessage("Debes ingresar ambas contraseñas");
            return false;
        }
        if (!(txtPasswd1.getText().toString().equals(txtPasswd2.getText().toString())) || isStringNull(provider)) {
            displayMessage("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getExtras() {
        Intent intent = getIntent();
        if (intent.hasExtra("mail")) {
            txtEmail.setText(intent.getCharSequenceExtra("mail").toString());
        }
        if (intent.hasExtra("displayName")) {
            txtName.setText(intent.getCharSequenceExtra("displayName").toString().split(" ")[0]);
            txtSurname.setText(intent.getCharSequenceExtra("displayName").toString().split(" ")[1]);
        }
        if (intent.hasExtra("provider")) {
            provider = intent.getStringExtra("provider");

        }
    }

    private void register() {
        btnRegister.setEnabled(false);
        if (validateFields()) {
            mProgressBar.setVisibility(View.VISIBLE);
            if (provider == null) {
                final String email = txtEmail.getText().toString();
                final String passwd = txtPasswd1.getText().toString();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, passwd);
                                user = task.getResult().getUser();
                                saveClient();
                            }
                        });
            } else {
                saveClient();
            }
        }
    }


    private void saveClient() {
        Log.d(LOG_LABEL, "Inicia guardar cliente");
        User newUser = new User();
        newUser.setEmail(txtEmail.getText().toString());
        newUser.setPremium(false);
        newUser.setDni(txtDNI.getText().toString());
        newUser.setName(txtName.getText().toString());
        newUser.setSurname(txtSurname.getText().toString());
        newUser.setGender(spinner.getText().toString());
        newUser.setBirthday(myCalendar.getTimeInMillis());
        newUser.setUserId(FirebaseAuth.getInstance().getUid());
        HashMap<String, Object> client = newUser.toMap();
        if (!isStringNull(provider))
            client.put("provider", provider);
        FirebaseFirestore.getInstance().collection("clients")
                .add(client)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(LOG_LABEL, "Cliente se guardo en firebase");
                        sendEmailVerification();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LOG_LABEL, "Cliente no se guardo en firebase: ", e.getCause());
                btnRegister.setEnabled(true);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void logInIntent() {
        Log.d(LOG_LABEL, "Inicia LogIn Intent");
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("email", txtEmail.getText().toString());
        intent.putExtra("confirmation", true);
        startActivity(intent);
        finish();
    }

    private void setupFirebaseAuth() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(LOG_LABEL, "FirebaseAuth, user no null");
                }
            }
        };
    }

    private void initElements() {
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Registro");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        btnRegister = findViewById(R.id.btnRegister);
        mProgressBar = findViewById(R.id.register_progressbar);
        mProgressBar.setVisibility(View.GONE);
        txtEmail = findViewById(R.id.txtEmail);
        txtPasswd1 = findViewById(R.id.txtPasswd);
        txtPasswd2 = findViewById(R.id.txtPasswd2);
        txtDNI = findViewById(R.id.txtDNI);
        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtBirthdate = findViewById(R.id.txtBirthdate);
        mContext = RegisterActivity.this;
        spinner = findViewById(R.id.spinner);
        setCategoryRequest();
    }

    private void setCategoryRequest() {
        ArrayList<String> sexos = new ArrayList<>();
        sexos.add("Femenino");
        sexos.add("Masculino");
        sexos.add("Otro");
        adapterSex = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_selectable_list_item, sexos);
        spinner.setAdapter(adapterSex);
    }

    private void initCalendar() {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                txtBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        };

        txtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void sendEmailVerification() {
        Log.d(LOG_LABEL, "Iniciando envio de mail");

        if (user != null) {
            Log.d(LOG_LABEL, "Envio de mail, user != Null");
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            if (task.isSuccessful()) {
                                Log.d(LOG_LABEL, "Envio de mail, se envio mail");
                                logInIntent();

                            } else {
                                Log.d(LOG_LABEL, "Envio de mail, No se envio mail: " + task.getException());
                                displayMessage("Fallo al enviar el mail");
                                //logInIntent();
                            }
                        }
                    });
        } else {
            Log.d(LOG_LABEL, "Envio de mail, user == Null");
            btnRegister.setEnabled(true);
        }
    }
}
