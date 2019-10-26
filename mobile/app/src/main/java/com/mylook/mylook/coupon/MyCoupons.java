package com.mylook.mylook.coupon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Coupon;
import com.mylook.mylook.profile.AccountActivity;
import com.mylook.mylook.session.Session;

import java.util.ArrayList;
import java.util.HashMap;

public class MyCoupons extends AppCompatActivity {

    private RecyclerView myCoupons;
    private Context mContext;
    private CouponRecyclerViewAdapter adapter;
    private ArrayList<Coupon> coupons;
    private ProgressBar progressBar;
    public final static String TAG = "MyCoupons";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initElements();
    }


    private void initElements() {
        setContentView(R.layout.activity_my_coupons);
        myCoupons = findViewById(R.id.couponRecycler);
        progressBar = findViewById(R.id.progressBar);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        this.setTitle("Mis Cupones");
        mContext = MyCoupons.this;
        coupons = new ArrayList<Coupon>();
        adapter = new CouponRecyclerViewAdapter(mContext, coupons);
        myCoupons.setAdapter(adapter);
        myCoupons.setLayoutManager(new LinearLayoutManager(mContext));
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadElements();
    }

    private void loadElements() {
        initRecyclerView();
        String clientId = Session.clientId;
        coupons.clear();
        FirebaseFirestore.getInstance().collection(getResources().getString(R.string.vouchersCollection))
                .whereEqualTo("clientId", clientId)
                .get().addOnSuccessListener(l -> {

            for (DocumentSnapshot doc : l.getDocuments()) {
                Coupon newCoupon = doc.toObject(Coupon.class);
                FirebaseFirestore.getInstance().collection(getResources().getString(R.string.storesCollection)).document((String) doc.get("storeId"))
                        .get().addOnSuccessListener(storeTask -> {
                    newCoupon.setImgStoreUrl((String)storeTask.get("profilePh"));
                    newCoupon.setDocumentId(doc.getId());
                    coupons.add(newCoupon);
                    adapter.notifyDataSetChanged();
                    if(coupons.size() == l.getDocuments().size()){
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }

        });
    }


    private void initRecyclerView() {
        myCoupons = findViewById(R.id.couponRecycler);
        adapter = new CouponRecyclerViewAdapter(MyCoupons.this, coupons);
        myCoupons.setAdapter(adapter);
        myCoupons.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
