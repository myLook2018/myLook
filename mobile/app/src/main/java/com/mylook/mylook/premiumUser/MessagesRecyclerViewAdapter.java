package com.mylook.mylook.premiumUser;

import android.content.Context;
import android.content.Intent;
import android.os.health.TimerStat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.entities.DiffusionMessage;
import com.mylook.mylook.entities.Store;
import com.mylook.mylook.info.ArticleInfoActivity;
import com.mylook.mylook.storeProfile.StoreActivity;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "AnswersRecyclerViewAdapter";
    private boolean isProfile=false;
    private Context mContext;
    private List<DiffusionMessage> messageList;


    public MessagesRecyclerViewAdapter(Context mContext, List<DiffusionMessage> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }
    public MessagesRecyclerViewAdapter(Context mContext, List<DiffusionMessage> messageList, boolean isProfile) {
        this.mContext = mContext;
        this.messageList = messageList;
        this.isProfile =isProfile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view;
        if(isProfile){
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_message_card_profile, parent, false);
        }else {
             view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premium_message_card, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Log.d("CardAnswer", "onBindViewHolder: called.");

        final DiffusionMessage message = messageList.get(position);
        holder.message.setText(message.getMessage());

        String time = formatTimestamp(message.getCreationDate());
        holder.time.setText(time);
    }

    private String formatTimestamp(Timestamp time){
        Timestamp now = Timestamp.now();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(now.toDate());

        Calendar messageCalendar = Calendar.getInstance();
        messageCalendar.setTime(time.toDate());
        int dayDifference = nowCalendar.get(Calendar.DAY_OF_YEAR) - messageCalendar.get(Calendar.DAY_OF_YEAR);
        int monthDifference = nowCalendar.get(Calendar.MONTH) - messageCalendar.get(Calendar.MONTH);
        int hourDifference = nowCalendar.get(Calendar.HOUR_OF_DAY) - messageCalendar.get(Calendar.HOUR_OF_DAY);
        int minuteDifference = nowCalendar.get(Calendar.MINUTE) - messageCalendar.get(Calendar.MINUTE);
        int secondDifference = nowCalendar.get(Calendar.SECOND) - messageCalendar.get(Calendar.SECOND);
        hourDifference = (hourDifference < 0) ? (24 + hourDifference) : hourDifference;
        Log.e("dayDiff", ""+dayDifference);
        Log.e("month", ""+monthDifference);
        Log.e("hour", ""+hourDifference);
        Log.e("minute", ""+minuteDifference);
        Log.e("second", ""+secondDifference);
        if(dayDifference >= 30){
            return dayDifference >= 60? "Hace "+monthDifference+" meses": "Hace 1 mes";
        } else if (dayDifference > 1) {
            return (dayDifference > 1) ?"Hace "+dayDifference+" días" : "Hace 1 día";
        } else if (hourDifference > 0){
            return hourDifference > 1? "Hace "+hourDifference+" horas":" Hace 1 hora";
        } else if (minuteDifference > 0){
            return  minuteDifference >1? "Hace "+minuteDifference+" minutos":"Hace 1 minuto";
        } else
            return "Hace "+secondDifference+" segundos";
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            time = itemView.findViewById(R.id.message_time);

        }
    }
}
