package com.mylook.mylook.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Coupon;
import com.mylook.mylook.session.MainActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.util.Calendar;

public class CouponActivity extends AppCompatActivity {

    private String couponId;
    private Coupon coupon;
    private TextView title, description, code, duedate, storeName;
    private ImageView imgStore;
    private ProgressBar mProgressBarr;
    private boolean fromNotification = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeScreenFields();

        getRemoteCoupon();
    }

    private void initializeScreenFields() {
        setContentView(R.layout.activity_coupon);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Cupón");
        title = findViewById(R.id.couponTitle);
        description = findViewById(R.id.couponDescription);
        code = findViewById(R.id.couponCode);
        duedate = findViewById(R.id.couponDueDate);
        storeName = findViewById(R.id.couponStoreName);
        imgStore = findViewById(R.id.couponImage);
        mProgressBarr = findViewById(R.id.mProgressBar);
        mProgressBarr.setVisibility(View.VISIBLE);
    }

    private void getRemoteCoupon() {
        Intent thisIntent = getIntent();
        couponId = thisIntent.getStringExtra("couponId");
        if(thisIntent.hasExtra("fromDeepLink"))
            fromNotification = thisIntent.getBooleanExtra("fromDeepLink", false);
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.vouchersCollection)).document(couponId).
                get().addOnSuccessListener(l -> {
            try {
                coupon = l.toObject(Coupon.class);
            } catch (Exception exception) {
                coupon = new Coupon();
                coupon.setCode((String) l.get("code"));
                coupon.setTitle((String) l.get("title"));
                coupon.setDescription((String) l.get("description"));
                coupon.setDueDate((Timestamp) l.get("dueDate"));
                coupon.setStoreId((String) l.get("storeId"));
                coupon.setStoreName((String) l.get("storeName"));
                coupon.setClientId((String) l.get("clientId"));
                coupon.setVoucherType((int)(long) l.get("voucherType"));
                coupon.setUsed((boolean)l.get("used"));
                coupon.setUsedDate((Timestamp)l.get("usedDate"));
                coupon.setStartDate((Timestamp)l.get("startDate"));
            }
            setClickListeners();
            setCouponValues();
        });
    }

    private String formatDate(Timestamp remoteDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(remoteDate.toDate());
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return calendar.get(Calendar.DAY_OF_MONTH) + " de " + meses[calendar.get(Calendar.MONTH)];
    }

    private void setClickListeners(){
        storeName.setOnClickListener(onClick -> {
            Intent storeIntent = new Intent(getApplicationContext(), StoreActivity.class);
            storeIntent.putExtra("store", coupon.getStoreName());
            startActivity(storeIntent);
        });
    }

    private void setCouponValues() {
        title.setText(coupon != null ? coupon.getTitle() : "Super mega 99,99% de descuento");
        description.setText((coupon != null ? coupon.getDescription() : "Esta sería la descripción del cupón. Si Mateo hubiese hecho la cloud function se podría probar" +
                ", pero adivinen quien no hizo la cloud function ehhhhhh"));
        code.setText(coupon != null ? coupon.getCode() : "0512RECI");
        if(coupon.isUsed()){
            duedate.setText("Usaste este cupón el "+formatDate(coupon.getUsedDate()));
        } else {
            if(coupon.getStartDate().compareTo(Timestamp.now()) > 0 ){
                duedate.setText("Cupón válido desde el "+formatDate(coupon.getStartDate()) +" hasta el "+formatDate(coupon.getDueDate()));
            } else {
                duedate.setText(coupon != null ? "Válido hasta el "+formatDate(coupon.getDueDate()) : "Vence el 19/12");
            }
        }
        storeName.setText(coupon != null ? coupon.getStoreName() : "AUKA siempre AUKA");
        code.setVisibility(View.VISIBLE);
        imgStore.setVisibility(View.VISIBLE);
        this.setTitle("Cupón " + getResources().getStringArray(R.array.voucherTypes)[coupon.getVoucherType()]);
        mProgressBarr.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(fromNotification){
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        } else{
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
