package com.mylook.mylook.login;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.home.MyLookActivity;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName, txtSurname, txtBirthdate;
    private ConstraintLayout mLayout;
    private MaterialBetterSpinner spinner;
    private Button btnRegister;
    private FirebaseUser user;
    private FirebaseFirestore dB;
    final Calendar myCalendar = Calendar.getInstance();
    private String provider = null;
    private boolean validMail = true;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        mProgressBar.setVisibility(View.GONE);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Registro");
        setSupportActionBar(tb);
        setupFirebaseAuth();
        btnRegister.setOnClickListener(v -> register());
        getExtras();
        initCalendar();
        txtEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                checkExistingEmail(txtEmail.getText().toString());
            } else {
                txtEmail.setTextColor(getResources().getColor(R.color.black));
                validMail = true;
            }
        });
        if(provider!=null)
            disableFields();

    }

    private void checkExistingEmail(String email){
        btnRegister.setEnabled(false);
        dB.collection("clients").whereEqualTo("email", email).get()
                .addOnCompleteListener(task -> {
                        Log.e("Task Result", task.getResult().getDocuments().toString());
                        if(task.getResult().getDocuments().size()>0){
                            validMail = false;
                            txtEmail.setTextColor(getResources().getColor(R.color.red));
                            Toast.makeText(RegisterActivity.this, "El mail ya está en uso", Toast.LENGTH_LONG ).show();
                        } else{
                            btnRegister.setEnabled(true);
                        }
                        mProgressBar.setVisibility(View.GONE);

                });
    }

    private void disableFields(){
        txtPasswd1.setEnabled(false);
        txtPasswd1.setText("****************");
        txtPasswd2.setEnabled(false);
        txtPasswd2.setText("****************");
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private boolean validateFields() {
        if (isStringNull(txtEmail.getText().toString())) {
            displayMessage("El campo Email es obligatorio");
            return false;
        }
        if(!validMail){
            displayMessage("El Mail seleccionado ya está en uso");
            return false;
        }
        if (isStringNull(txtPasswd1.getText().toString())&&isStringNull(provider)) {
            displayMessage("Debes ingresar una contraseña");
            return false;
        }
        if (txtPasswd1.getText().length() < 6 && isStringNull(provider)) {
            displayMessage("La contraseña debe contener 6 caracteres");
            txtPasswd1.setText("");
            txtPasswd2.setText("");
            return false;
        }
        if (isStringNull(txtPasswd2.getText().toString())&&isStringNull(provider)) {
            displayMessage("Debes ingresar ambas contraseñas");
            return false;
        }
        if (!txtPasswd1.getText().toString().equals(txtPasswd2.getText().toString())&&isStringNull(provider)) {
            displayMessage("Las contraseñas no coinciden");
            return false;
        }
        if (isStringNull(txtName.getText().toString())) {
            displayMessage("El campo Nombre es obligatorio");
            return false;
        }
        if (isStringNull(txtSurname.getText().toString())) {
            displayMessage("El campo Apellido es obligatorio");
            return false;
        }
        if (isStringNull(txtBirthdate.getText().toString())) {
            displayMessage("Debes ingresar una Fecha de Nacimiento");
            return false;
        }
        if (isStringNull(txtDNI.getText().toString())) {
            displayMessage("Debes ingresar un DNI");
            return false;
        }

        Calendar today = Calendar.getInstance();
        int diff = today.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);
        if(diff<14){
            displayMessage("Debes tener al menos 14 años");
            return false;
        }
       /* if(spinner.getSelectedItemPosition()==0){
            displayMessage("El campo Sexo es obligatorio");
            return false;
        }*/
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
        if (validateFields()) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLayout.setVisibility(View.GONE);
            if (provider==null) {
                String email = txtEmail.getText().toString();
                String passwd = txtPasswd1.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Log.d("REGISTER", "Esta pro guardar datos");
                                boolean saved = saveClient();
                                setupFirebaseAuth();
                            } else {
                                Log.w("REGISTER", "createUserWithEmail:Failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Algo salio mal",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                boolean saved = saveClient();
            }
        }
    }

    private boolean saveClient() {
        final boolean[] saved = new boolean[1];
        final Map<String, Object> client = new HashMap<>();
        client.put("email", txtEmail.getText().toString());
        client.put("dni", txtDNI.getText().toString());
        client.put("name", txtName.getText().toString());
        client.put("surname", txtSurname.getText().toString());
        client.put("birthday", txtBirthdate.getText().toString());
        client.put("gender", spinner.getText().toString());
        client.put("userId", mAuth.getUid());
        client.put("isPremium", false);
        if(!isStringNull(provider))
            client.put("provider", provider);
        dB.collection("clients")
                .add(client).addOnSuccessListener(documentReference -> {
                    Log.d("SAVE_CLIENT", "Se guarda client");
                    saved[0] = true;
                    sendEmailVerification();
                    logInIntent();
                }).addOnFailureListener(e -> {
                    Log.e("SAVE_Client", "No se guarda cliente ", e.getCause());

                    saved[0] = false;
                    Toast.makeText(RegisterActivity.this, "Algo salio mal",
                            Toast.LENGTH_SHORT).show();
                });
        return saved[0];
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void logInIntent() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra("email", txtEmail.getText().toString());
        intent.putExtra("confirmation",true);
        startActivity(intent);
        finish();
    }

    private void setupFirebaseAuth() {
        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(mContext, MyLookActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    private void initElements() {
        dB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        mProgressBar =findViewById(R.id.progressbar);
        txtEmail = findViewById(R.id.txtEmail);
        txtPasswd1 = findViewById(R.id.txtPasswd);
        txtPasswd2 = findViewById(R.id.txtPasswd2);
        txtDNI = findViewById(R.id.txtDNI);
        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtBirthdate = findViewById(R.id.txtBirthdate);
        mLayout = findViewById(R.id.register_form);
        mContext = RegisterActivity.this;
        spinner = findViewById(R.id.spinner);
        setCategoryRequest();
    }

    private void setCategoryRequest() {
        dB.collection("categories").whereEqualTo("name", "sexo").get().addOnCompleteListener(task -> {
            ArrayList<String> categories = (ArrayList<String>) task.getResult().getDocuments().get(0).get("categories");
            spinner.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_selectable_list_item, categories));
        });
    }

    private void initCalendar(){

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            txtBirthdate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        };

        txtBirthdate.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    // [START_EXCLUDE]
                    // Re-enable button
                    if (task.isSuccessful()) {
                        Log.d("EMAIL_VERIFICATION", "Se verifico mail");
                        Toast.makeText(RegisterActivity.this,
                                "Verifica tu mail y luego inicia sesion ",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("EMAIL_VERIFICATION", "No se verifico mail", task.getException());
                        Log.e("SendEmailVerification", "sendEmailVerification", task.getException());
                        Toast.makeText(RegisterActivity.this,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show();
                    }
                    // [END_EXCLUDE]
                });
        // [END send_email_verification]
    }
}
