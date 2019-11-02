package com.mylook.mylook.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.mylook.mylook.R;
import com.mylook.mylook.coupon.MyCouponsActivity;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.session.Session;

public class AccountActivity extends AppCompatActivity {

    private static final int PREMIUM_REQUEST = 2;
    private static final int SUCCESS_CODE = 0;
    private static final int CHANGE_USER =1;
    private TextView txtName;
    private TextView txtEmail;
    private ImageView imageDestacado;
    private TextView txtDestacado;
    private ImageView imageAccount;
    private TextView txtAccount;
    private ImageView imageExit;
    //private TextView txtNotifications;
    //private ImageView imageNotifications;
    private TextView txtExit;
    private Context mContext;
    private String clientId;
    private TextView txtCoupons;
    private ImageView imgCoupons;
    public final static String TAG = "AccountActivity";


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
        Log.e(TAG,"ON RESUME ACCOUNT ACTIVITY");
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
        //imageNotifications = findViewById(R.id.img_notificaciones);
        //txtNotifications = findViewById(R.id.txtemail);
        imageDestacado =findViewById(R.id.image_destacado);
        txtDestacado = findViewById(R.id.txtSettingsDest);
        imageExit = findViewById(R.id.image_exit);
        txtExit = findViewById(R.id.txtExit);
        txtCoupons = findViewById(R.id.txtCoupons);
        imgCoupons = findViewById(R.id.image_coupon);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHANGE_USER){
            if(resultCode== SUCCESS_CODE){
                Session.setChange(data.getStringExtra("name"));
                txtName.setText(Session.name);
                //txtEmail.setText(Session.mail);
                Log.e(TAG, "El usuario cambio");
            }
        }else if(requestCode==2){
            if (resultCode==SUCCESS_CODE){
                //finish();
                Log.e(TAG, "Paso a destacado success");
                txtDestacado.setVisibility(View.GONE);
                imageDestacado.setVisibility(View.GONE);
            }
        }
    }


    private void setOnClickListener() {

        txtAccount.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), EditInfoActivity.class);
            startActivityForResult(intent,CHANGE_USER);

        });

        imageAccount.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditInfoActivity.class);
            startActivityForResult(intent,CHANGE_USER);
        });
        txtCoupons.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), MyCouponsActivity.class);
            startActivity(intent);

        });

        imgCoupons.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MyCouponsActivity.class);
            startActivity(intent);
        });

        /*txtNotifications.setOnClickListener(v -> {
            //TODO Definir que hacemos con las notis
        });

        imageNotifications.setOnClickListener(v -> {
            //TODO Definir que hacemos con las notis
        });*/

        imageDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", Session.name);
            startActivityForResult(intent, PREMIUM_REQUEST);

        });
        txtDestacado.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PremiumRequestActivity.class);
            intent.putExtra("clientId", clientId);
            intent.putExtra("userName", Session.name);
            startActivityForResult(intent, PREMIUM_REQUEST);

        });


        txtExit.setOnClickListener(v -> {
            DialogManager dm = DialogManager.getInstance();

            dm.createLogoutDialog(mContext,
                    "Cerrar Sesion",
                    "¿Estas seguro que quieres cerrar sesion?",
                    "Cerrar",
                    "Cancelar").show();
        });

        imageExit.setOnClickListener(v -> {
            DialogManager dm = DialogManager.getInstance();

            dm.createLogoutDialog(
                    mContext,
                    "Cerrar Sesion",
                    "¿Estas seguro que quieres cerrar sesion?",
                    "Cerrar",
                    "Cancelar").show();
        });


    }

    private void setUserProfile() {
        Session.updateData();
        txtName.setText(Session.name);
        txtEmail.setText(Session.mail);
        clientId = Session.clientId;
        if(Session.isPremium){
            imageDestacado.setVisibility(View.GONE);
            txtDestacado.setVisibility(View.GONE);
        }else{
            imageDestacado.setVisibility(View.VISIBLE);
            txtDestacado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
