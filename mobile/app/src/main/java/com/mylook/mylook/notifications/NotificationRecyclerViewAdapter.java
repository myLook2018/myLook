package com.mylook.mylook.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Notification;


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
            Glide.with(mContext).asBitmap().load(notif.getImageUrl()).into(holder.rigthPhoto);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView leftPhoto;
        TextView notificationMessage;
        TextView dayMessage;
        ImageView rigthPhoto;
        TextView notificationName;

        public ViewHolder(View itemView) {
            super(itemView);
            leftPhoto = itemView.findViewById(R.id.leftPhoto);
            rigthPhoto= itemView.findViewById(R.id.rightPhoto);
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
