package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.session.Sesion;

public class AccountActivity extends AppCompatActivity {

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

    private void initElements() {
        setContentView(R.layout.activity_profile);
        ((Toolbar)findViewById(R.id.toolbar)).setTitle("Mi Cuenta");
        mContext = AccountActivity.this;
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        imageAccount = findViewById(R.id.image_account);
        txtAccount = findViewById(R.id.txtAccount);
        imageNotifications = findViewById(R.id.img_notificaciones);
        txtNotifications = findViewById(R.id.txtNotifications);
        imageDestacado =findViewById(R.id.image_destacado);
        txtDestacado = findViewById(R.id.txtSettingsDest);
        imageHelp = findViewById(R.id.image_help);
        txtHelp = findViewById(R.id.txtHelp);
        imageExit = findViewById(R.id.image_exit);
        txtExit = findViewById(R.id.txtExit);
    }


    private void setOnClickListener() {

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), EditInfoActivity.class);
                startActivity(intent);
            }
        });

        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditInfoActivity.class);
                startActivity(intent);
            }
        });

        txtNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir que hacemos con las notis
            }
        });

        imageNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir que hacemos con las notis
            }
        });

        imageDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", Sesion.name);
                startActivity(intent);

            }
        });
        txtDestacado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PremiumRequestActivity.class);
                intent.putExtra("clientId", clientId);
                intent.putExtra("userName", Sesion.name);
                startActivity(intent);

            }
        });

        imageHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir help
            }
        });
        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Definir help
            }
        });

        txtExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dm = DialogManager.getInstance();

                dm.createLogoutDialog(mContext,
                        "Cerrar Sesion",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show();
            }
        });

        imageExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager dm = DialogManager.getInstance();

                dm.createLogoutDialog(
                        mContext,
                        "Cerrar Sesion",
                        "¿Estas seguro que quieres cerrar sesion?",
                        "Si",
                        "No").show();
            }
        });


    }

    private void setUserProfile() {
        isPremiumUser= Sesion.isPremium;
        txtName.setText(Sesion.name);
        txtEmail.setText(Sesion.mail);
        clientId = Sesion.clientId;
        if(isPremiumUser){
            imageDestacado.setVisibility(View.GONE);
            txtDestacado.setVisibility(View.GONE);
        }else{
            imageDestacado.setVisibility(View.VISIBLE);
            txtDestacado.setVisibility(View.VISIBLE);
        }
    }

}
