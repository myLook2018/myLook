package com.mylook.mylook.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.entities.User;
import com.mylook.mylook.session.Session;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;


public class EditInfoActivity extends AppCompatActivity {

    private EditText txtName, txtSurname, txtBirthdate;
    private MaterialBetterSpinner cmbSexo;
    private TextView txtChangePassword;
    private ImageView btnSaveChanges;
    private User oldUser = null;
    private ProgressBar mProgressBar;
    private User userInDB;
    private int USER_CHANGED = 1, USER_NOT_CHANGED = 2;
    private EditText txtDNI;
    final Calendar myCalendar = Calendar.getInstance();

    public EditInfoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initElements();
        initCalendar();
        setData();
        setOnClickListener();
    }

    private void initCalendar() {

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            txtBirthdate.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
        };

        txtBirthdate.setOnClickListener(view -> new DatePickerDialog(EditInfoActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void setOnClickListener() {

        btnSaveChanges.setOnClickListener(v -> {
            if (validateFields()) {
                final User newUser = new User();

                newUser.setBirthday(myCalendar.getTimeInMillis());
                newUser.setEmail(Session.mail); //cambiar aparte
                newUser.setDni(txtDNI.getText().toString()); //cambiar aparte
                newUser.setName(txtName.getText().toString());
                newUser.setSurname(txtSurname.getText().toString());
                newUser.setGender(cmbSexo.getText().toString());
                newUser.setUserId(oldUser.getUserId());
                newUser.setInstallToken(oldUser.getInstallToken());
                newUser.setPremium(oldUser.getPremium());
                if (newUser.compareTo(oldUser) != 0) {
                    FirebaseFirestore.getInstance().collection("clients")
                            .whereEqualTo("userId", oldUser.getUserId()).get()
                            .addOnCompleteListener(task ->
                                    FirebaseFirestore.getInstance().collection("clients")
                                            .document(task.getResult().getDocuments().get(0).getId())
                                            .set(newUser.toMap(), SetOptions.merge())
                                            .addOnCompleteListener(task1 -> {
                                                Toast.makeText(EditInfoActivity.this, "Tu usuario ha sido actualizado", Toast.LENGTH_LONG).show();
                                                Session.updateData();
                                                Intent returnIntent = new Intent();
                                                returnIntent.putExtra("name", txtName.getText());
                                                returnIntent.putExtra("email", "gisigimenez@blabla");
                                                setResult(USER_CHANGED, returnIntent);
                                                finish();
                                            }));
                } else {
                    setResult(USER_NOT_CHANGED);
                    finish();
                }
            }
        });
        txtChangePassword.setOnClickListener(v -> {
            DialogManager dm = new DialogManager();
            dm.createChangePasswordDialog(
                    EditInfoActivity.this,
                    "Cambiar Contraseña",
                    "¿Estas seguro que quieres cambiar tu contraseña?",
                    "Si",
                    "No").show();

        });
    }

    private void initElements() {
        setContentView(R.layout.activity_account_info);
        Toolbar tb = findViewById(R.id.info_account_toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Editar Información");
        if (Session.isPremium) {
            TextView txtChangePhoto = findViewById(R.id.lblChangePhoto);
            txtChangePhoto.setVisibility(View.VISIBLE);
        }
        txtName = findViewById(R.id.txtName);
        txtSurname = findViewById(R.id.txtSurname);
        txtDNI = findViewById(R.id.txtDNI);
        txtBirthdate = findViewById(R.id.txtBirthdate);
        cmbSexo = findViewById(R.id.sexo);
        loadCmbSexo();
        txtChangePassword = findViewById(R.id.txtPassword);

        btnSaveChanges = findViewById(R.id.btn_changePassword);
        mProgressBar = findViewById(R.id.progressbar);
    }

    private void loadCmbSexo() {
        ArrayList<String> sexos = new ArrayList<>();
        sexos.add("Femenino");
        sexos.add("Masculino");
        sexos.add("Otro");
        cmbSexo.setAdapter(new ArrayAdapter<>(EditInfoActivity.this, android.R.layout.simple_selectable_list_item, sexos));
    }

    private void setData() {
        Log.e("EDIT INFO ACTIVITY ", "set data");

        FirebaseFirestore.getInstance().collection("clients").document(Session.clientId)
                .get().addOnCompleteListener(task -> {
                    userInDB = task.getResult().toObject(User.class);
                    if (userInDB != null) {
                        //txtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        //txtEmail.setEnabled(false);
                        txtDNI.setText(userInDB.getDni());
                        txtName.setText(userInDB.getName());
                        txtSurname.setText(userInDB.getSurname());
                        myCalendar.setTimeInMillis(userInDB.getBirthday());
                        int mes = myCalendar.get(Calendar.MONTH) + 1;
                        final String dateFormat = myCalendar.get(Calendar.DAY_OF_MONTH) + "/" + mes + "/" + myCalendar.get(Calendar.YEAR);
                        txtBirthdate.setText(dateFormat);
                        cmbSexo.setText(userInDB.getGender());
                        oldUser = userInDB;
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean validateFields() {
       /* if (isStringNull(txtEmail.getText().toString())) {
            displayMessage("El campo Email es obligatorio");
            return false;
        }
        if(!validMail){
            displayMessage("El Mail seleccionado ya está en uso");
            return false;
        }*/

        if (isStringNull(txtName.getText().toString())) {
            displayMessage("El campo Nombre es obligatorio");
            return false;
        }
        if (isStringNull(txtSurname.getText().toString())) {
            displayMessage("El campo Apellido es obligatorio");
            return false;
        }
        if (isStringNull(txtDNI.getText().toString())) {
            displayMessage("Debes ingresar un DNI");
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
        if (isStringNull(cmbSexo.getText().toString())) {
            displayMessage("El campo Sexo es obligatorio");
            return false;
        }
        return true;
    }

    private boolean isStringNull(String string) {
        return "".equals(string);
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}


