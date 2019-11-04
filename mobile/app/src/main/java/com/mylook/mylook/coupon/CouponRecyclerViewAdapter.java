package com.mylook.mylook.coupon;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Coupon;
import com.mylook.mylook.recommend.AnswersRecyclerViewAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CouponRecyclerViewAdapter extends RecyclerView.Adapter<CouponRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Coupon> coupons;
    private RecyclerView recyclerView;

    public CouponRecyclerViewAdapter(Context mContext, List<Coupon> coupons) {
        this.mContext = mContext;
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_card, parent, false);
        ViewHolder holder = new CouponRecyclerViewAdapter.ViewHolder(view);
        view.setOnClickListener(listener -> {
            Intent newIntent = new Intent(mContext, CouponActivity.class);
            newIntent.putExtra("couponId", holder.documentId);
            mContext.startActivity(newIntent);
        });
        return holder;
    }

    private String formatDate(Timestamp remoteDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(remoteDate.toDate());
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return "Vence el " + calendar.get(Calendar.DAY_OF_MONTH) + " de " + meses[calendar.get(Calendar.MONTH)];
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Coupon coupon = coupons.get(position);
        Glide.with(mContext).asBitmap().load(coupon.getImgStoreUrl()).into(holder.imgStore);
        holder.couponTitle.setText(coupon.getTitle());
        if (coupon.isUsed()) {
            holder.couponDueDate.setText("Ya usaste este cupón");
        } else {
            holder.couponDueDate.setText(formatDate(coupon.getDueDate()));
        }
        holder.documentId = coupon.getDocumentId();

    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStore;
        TextView couponTitle;
        TextView couponDueDate;
        String documentId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStore = itemView.findViewById(R.id.imgStore);
            couponTitle = itemView.findViewById(R.id.couponTitle);
            couponDueDate = itemView.findViewById(R.id.couponDueDate);
        }

    }
}
