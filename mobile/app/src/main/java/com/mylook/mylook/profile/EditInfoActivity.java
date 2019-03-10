package com.mylook.mylook.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.entities.User;
import com.mylook.mylook.login.RegisterActivity;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;

public class EditInfoActivity extends AppCompatActivity {

    private EditText txtEmail, txtPasswd1, txtPasswd2, txtDNI, txtName, txtSurname, txtBirthdate;
    private TextView txtChangePassword;
    private LinearLayout mLayout;
    private MaterialBetterSpinner spinner;
    private Button btnSaveChanges;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private Toolbar tb;
    private User oldUser = null;
    private ProgressBar mProgressBar;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initElements();
        initCalendar();
        setOnClickListener();
        mContext = getApplicationContext();
        // setupBottomNavigationView();
    }

    private void initCalendar() {

        final Calendar myCalendar = Calendar.getInstance();
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
                new DatePickerDialog(EditInfoActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setOnClickListener() {
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User newUser = new User();
                newUser.setBirthday(txtBirthdate.getText().toString());
                newUser.setEmail(txtEmail.getText().toString());
                newUser.setDni(txtDNI.getText().toString());
                newUser.setName(txtName.getText().toString());
                newUser.setSurname(txtSurname.getText().toString());
                newUser.setGender(spinner.getText().toString());
                newUser.setUserId(oldUser.getUserId());

                if (newUser.compareTo(oldUser) != 0) {
                    dB.collection("clients").whereEqualTo("userId", oldUser.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            dB.collection("clients").document(task.getResult().getDocuments().get(0).getId()).set(newUser.toMap(), SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(EditInfoActivity.this, "Tu usuario ha sido actualizado", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EditInfoActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }

            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dm = DialogManager.getInstance();
                dm.createChangePasswordDialog(
                        EditInfoActivity.this,
                        "Cambiar Contraseña",
                        "¿Estas seguro que quieres cambiar tu contraseña?",
                        "Si",
                        "No").show();

            }
        });

    }

    private void initElements() {
        txtEmail = findViewById(R.id.txtEmail);
        txtPasswd1 = findViewById(R.id.txtPasswd);
        txtPasswd2 = findViewById(R.id.txtPasswd2);
        txtDNI = findViewById(R.id.txtDNI);
        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtBirthdate = findViewById(R.id.txtBirthdate);
        btnSaveChanges = findViewById(R.id.btnRegister);
        btnSaveChanges.setText("Aplicar");
        spinner = findViewById(R.id.spinner);
        mProgressBar = findViewById(R.id.register_progressbar);
        txtChangePassword = findViewById(R.id.changePassword);
        txtChangePassword.setVisibility(View.VISIBLE);
        setData();
        setCategoryRequest();
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Editar Información");
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void setCategoryRequest() {
        dB.collection("categories").whereEqualTo("name", "sexo").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> categories = (ArrayList<String>) task.getResult().getDocuments().get(0).get("categories");
                spinner.setAdapter(new ArrayAdapter<String>(EditInfoActivity.this, android.R.layout.simple_selectable_list_item, categories));

            }
        });
    }

    private void setData() {
        dB.collection("clients").whereEqualTo("userId", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().getDocuments().size() > 0) {
                    User currentUser = task.getResult().getDocuments().get(0).toObject(User.class);
                    txtEmail.setText(currentUser.getEmail());
                    txtEmail.setEnabled(false);
                    txtDNI.setText(currentUser.getDni());
                    txtName.setText(currentUser.getName());
                    txtSurname.setText(currentUser.getSurname());
                    txtBirthdate.setText(currentUser.getBirthday());
                    spinner.setText(currentUser.getGender());
                    txtPasswd1.setText("*********");
                    txtPasswd1.setEnabled(false);
                    txtPasswd2.setText("*********");
                    txtPasswd2.setEnabled(false);
                    oldUser = currentUser;

                }


                mProgressBar.setVisibility(View.GONE);


            }
        });
    }


}
