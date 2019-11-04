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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Coupon;
import com.mylook.mylook.session.Session;

import java.util.ArrayList;

public class MyCouponsActivity extends AppCompatActivity {

    private RecyclerView myCoupons;
    private Context mContext;
    private CouponRecyclerViewAdapter adapter;
    private ArrayList<Coupon> coupons;
    private ProgressBar progressBar;
    public final static String TAG = "MyCouponsActivity";

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
        mContext = MyCouponsActivity.this;
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
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(l -> {
            if(l.getDocuments().size() == 0){
                progressBar.setVisibility(View.GONE );
            } else {
                for (DocumentSnapshot doc : l.getDocuments()) {
                    Coupon middleCoupon;
                    try {
                        middleCoupon = doc.toObject(Coupon.class);
                    } catch (Exception exception) {
                        middleCoupon = new Coupon();
                        middleCoupon.setCode((String) doc.get("code"));
                        middleCoupon.setTitle((String) doc.get("title"));
                        middleCoupon.setDescription((String) doc.get("description"));
                        middleCoupon.setDueDate((Timestamp) doc.get("dueDate"));
                        middleCoupon.setStoreId((String) doc.get("storeId"));
                        middleCoupon.setStoreName((String) doc.get("storeName"));
                        middleCoupon.setClientId((String) doc.get("clientId"));
                        middleCoupon.setUsed((boolean)doc.get("used"));

                    }
                    Coupon newCoupon = middleCoupon;
                    FirebaseFirestore.getInstance().collection(getResources().getString(R.string.storesCollection)).document((String) doc.get("storeId"))
                            .get().addOnSuccessListener(storeTask -> {
                        newCoupon.setImgStoreUrl((String) storeTask.get("profilePh"));
                        newCoupon.setDocumentId(doc.getId());
                        coupons.add(newCoupon);
                        adapter.notifyDataSetChanged();
                        if (coupons.size() == l.getDocuments().size()) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }


    private void initRecyclerView() {
        myCoupons = findViewById(R.id.couponRecycler);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
