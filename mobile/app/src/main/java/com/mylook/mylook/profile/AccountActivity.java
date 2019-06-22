package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.session.Session;

public class AccountActivity extends AppCompatActivity {

    private int USER_CHANGED = 1;
    private TextView txtName;
    private TextView txtEmail;
    private ImageView imageDestacado;
    private TextView txtDestacado;
    private ImageView imageAccount;
    private TextView txtAccount;
    private ImageView imageHelp;
    private TextView txtHelp;
    private ImageView imageExit;
    private TextView txtNotifications;
    private ImageView imageNotifications;
    private TextView txtExit;
    private Context mContext;
    private String clientId;
    private boolean isPremiumUser;
    public final static String TAG = "ProfileFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initElements();
        setUserProfile();
        setOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initElements();
        setUserProfile();
        setOnClickListener();
    }

    private void initElements() {
        setContentView(R.layout.activity_account);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Mi Cuenta");
        mContext = AccountActivity.this;
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        imageAccount = findViewById(R.id.image_account);
        txtAccount = findViewById(R.id.txtPrivAccount);
        imageNotifications = findViewById(R.id.img_notificaciones);
        txtNotifications = findViewById(R.id.txtemail);
        imageDestacado = findViewById(R.id.image_destacado);
        txtDestacado = findViewById(R.id.txtSettingsDest);
        imageHelp = findViewById(R.id.image_help);
        txtHelp = findViewById(R.id.txtemail);
        imageExit = findViewById(R.id.image_exit);
        txtExit = findViewById(R.id.txtExit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == USER_CHANGED) {
                Session.updateData();
                txtName.setText(data.getCharSequenceExtra("name"));
                txtEmail.setText(data.getCharSequenceExtra("email"));
                Log.e("ACCOUNT ACTIVITY", "El usuario cabio");
                //onResume();
            }
        }
    }

    private void setOnClickListener() {
        txtAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EditInfoActivity.class);
            startActivityForResult(intent, 1);
        });
        imageAccount.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditInfoActivity.class);
            startActivity(intent);
        });
        txtNotifications.setOnClickListener(v -> {
            //Definir que hacemos con las notis
        });
        imageNotifications.setOnClickListener(v -> {
            //Definir que hacemos con las notis
        });
        imageDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", Session.name);
            startActivity(intent);
        });
        txtDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", Session.name);
            startActivity(intent);
        });
        imageHelp.setOnClickListener(v -> {
            //Definir help
        });
        txtHelp.setOnClickListener(v -> {
            //Definir help
        });
        txtExit.setOnClickListener(v ->
                DialogManager.createLogoutDialog(mContext,
                        "Cerrar Session",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show()
        );
        imageExit.setOnClickListener(v ->
                DialogManager.createLogoutDialog(mContext,
                        "Cerrar Session",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show()
        );
    }

    private void setUserProfile() {
        isPremiumUser = Session.isPremium;
        txtName.setText(Session.name);
        txtEmail.setText(Session.mail);
        clientId = Session.clientId;
        if (isPremiumUser) {
            imageDestacado.setVisibility(View.GONE);
            txtDestacado.setVisibility(View.GONE);
        } else {
            imageDestacado.setVisibility(View.VISIBLE);
            txtDestacado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
