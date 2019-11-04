package com.mylook.mylook.notifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.coupon.CouponActivity;
import com.mylook.mylook.entities.Notification;
import com.mylook.mylook.premiumUser.PremiumUserProfileActivity;
import com.mylook.mylook.recommend.RequestRecommendActivity;


import java.util.ArrayList;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Notification> items;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_center_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public NotificationRecyclerViewAdapter(){

    }

    public NotificationRecyclerViewAdapter(Context mContext, ArrayList<Notification> itemsM) {
        this.mContext = mContext;
        this.items = itemsM;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notif = items.get(position);
        holder.notificationMessage.setText(notif.getMessage());
        holder.notificationName.setText(notif.getPremiumUserName());
        Glide.with(mContext).asBitmap().load(notif.getUserPhotoUrl()).into(holder.leftPhoto);
        if(notif.getImageUrl() !=  null && !notif.getImageUrl().isEmpty())
            Glide.with(mContext).asBitmap().load(notif.getImageUrl()).into(holder.rightPhoto);
        if (notif.getOpenClass()!= null && !notif.getOpenClass().isEmpty() && notif.getElementId()!=null && !notif.getElementId().isEmpty()){
            holder.itemView.setOnClickListener( l-> {
                Class activity;
                String elementId;
                String premiumClass = holder.itemView.getContext().getResources().getString(R.string.PremiumUserClass);
                String requestClass = holder.itemView.getContext().getResources().getString(R.string.RecommendClass);
                String couponClass = holder.itemView.getContext().getResources().getString(R.string.CouponClass);
                elementId = notif.getElementId();
                Intent newIntent = null;
                if (notif.getOpenClass().equals(premiumClass)){
                    activity = PremiumUserProfileActivity.class;
                    newIntent = new Intent(holder.itemView.getContext(), activity);
                    newIntent.putExtra("clientId", elementId);
                } else if (notif.getOpenClass().equals(requestClass)){
                    activity = RequestRecommendActivity.class;
                    newIntent = new Intent(holder.itemView.getContext(), activity);
                    Glide.with(mContext).asDrawable().load(mContext.getResources().getDrawable(R.drawable.ic_recommend)).into(holder.rightPhoto);

                    holder.rightPhoto.setVisibility(View.VISIBLE);
                    newIntent.putExtra("requestId", elementId);
                } else if(notif.getOpenClass().equals(couponClass)){
                    activity = CouponActivity.class;
                    holder.rightPhoto.setVisibility(View.VISIBLE);
                    Glide.with(mContext).asDrawable().load(mContext.getResources().getDrawable(R.drawable.ic_coupon)).into(holder.rightPhoto);
                    newIntent = new Intent(holder.itemView.getContext(), activity);
                    newIntent.putExtra("couponId", elementId);
                }
                if(newIntent!=null)
                    Log.e("Notification Recycler", "Element Id: "+elementId);
                    Log.e("Notification Recycler", "Element Class: "+notif.getOpenClass());
                    holder.itemView.getContext().startActivity(newIntent);
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView leftPhoto;
        TextView notificationMessage;
        TextView dayMessage;
        ImageView rightPhoto;
        TextView notificationName;

        public ViewHolder(View itemView) {
            super(itemView);
            leftPhoto = itemView.findViewById(R.id.leftPhoto);
            rightPhoto = itemView.findViewById(R.id.rightPhoto);
            dayMessage = itemView.findViewById(R.id.dayMessage);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationName = itemView.findViewById(R.id.notificationName);
        }

    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
