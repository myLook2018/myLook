package com.mylook.mylook.coupon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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



    private void initElements(){
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
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadElements();
    }

    private void loadElements(){
        FirebaseFirestore.getInstance().collection("coupons").
                whereEqualTo("clientId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(l -> {
                        for(DocumentSnapshot doc: l.getDocuments()){
                            coupons.add(doc.toObject(Coupon.class));
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                });
        initRecyclerView();
    }


    private void initRecyclerView() {
        myCoupons = findViewById(R.id.couponRecycler);
        CouponRecyclerViewAdapter adapter = new CouponRecyclerViewAdapter(MyCoupons.this, coupons);
        myCoupons.setAdapter(adapter);
        myCoupons.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }
}
