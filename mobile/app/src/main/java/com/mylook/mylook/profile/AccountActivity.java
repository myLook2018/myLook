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
import com.mylook.mylook.coupon.MyCoupons;
import com.mylook.mylook.dialogs.DialogManager;
import com.mylook.mylook.premiumUser.PremiumRequestActivity;
import com.mylook.mylook.session.Session;

public class AccountActivity extends AppCompatActivity {

    private static final int PREMIUM_REQUEST = 2;
    private static final int SUCCESS_CODE = 0;
    private int USER_CHANGED=1;
    private TextView txtName;
    private TextView txtEmail;
    private ImageView imageDestacado;
    private TextView txtDestacado;
    private ImageView imageAccount;
    private TextView txtAccount;
    private ImageView imageExit;
    private TextView txtNotifications;
    private ImageView imageNotifications;
    private TextView txtExit;
    private Context mContext;
    private String clientId;
    private TextView txtCoupons;
    private ImageView imgCoupons;
    private boolean isPremiumUser;
    public final static String TAG = "PremiumOptionsFragment";


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
        Log.e("ACC ACTIVITY","ON RESUME ACCOUNT ACTIVITY");
        //initElements();
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
        if(requestCode==1){
            if(resultCode==USER_CHANGED){
                Session.updateData();
                txtName.setText(data.getCharSequenceExtra("name"));
                txtEmail.setText(data.getCharSequenceExtra("email"));
                Log.e("ACCOUNT ACTIVITY", "El usuario cabio");
                //onResume();
            }
        }else if(requestCode==2){
            if (resultCode==SUCCESS_CODE){
                finish();
                Log.e("ON ACTIVITY RES", "req 2 success");
                txtDestacado.setVisibility(View.GONE);
                imageDestacado.setVisibility(View.GONE);
            }
        }


    }


    private void setOnClickListener() {

        txtAccount.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), EditInfoActivity.class);
            startActivityForResult(intent,1);

        });

        imageAccount.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditInfoActivity.class);
            startActivity(intent);
        });
        txtCoupons.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), MyCoupons.class);
            startActivityForResult(intent,1);

        });

        imgCoupons.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MyCoupons.class);
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
                    "Si",
                    "No").show();
        });

        imageExit.setOnClickListener(v -> {
            DialogManager dm = DialogManager.getInstance();

            dm.createLogoutDialog(
                    mContext,
                    "Cerrar Sesion",
                    "¿Estas seguro que quieres cerrar sesion?",
                    "Si",
                    "No").show();
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
