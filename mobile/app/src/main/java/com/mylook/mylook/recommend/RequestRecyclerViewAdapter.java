package com.mylook.mylook.recommend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.graphics.ColorSpace.Model.RGB;

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RequestRecyclerViewAdapter";
    private Context mContext;
    private List<RequestRecommendation> requestRecommendationsList;


    public RequestRecyclerViewAdapter(Context mContext, List<RequestRecommendation> requestRecommendationsList) {
        this.mContext = mContext;
        this.requestRecommendationsList = requestRecommendationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_request_recommendation, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Log.d("ALGO", "onBindViewHolder: called.");

        final RequestRecommendation requestRecommendation = requestRecommendationsList.get(position);

        Glide.with(mContext).asBitmap().load(requestRecommendation.getRequestPhoto()).into(holder.requestPhoto);
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(requestRecommendation.getLimitDate());
        int mes=cal.get(Calendar.MONTH)+1;
        final String dateFormat=cal.get(Calendar.DAY_OF_MONTH)+"/"+mes + "/"+cal.get(Calendar.YEAR);
        Calendar today = Calendar.getInstance();
        int daysLeft = (int)TimeUnit.MILLISECONDS.toDays(cal.getTime().getTime() - today.getTime().getTime());
        holder.txtDate.setText("Faltan "+daysLeft+ " días");

        holder.titleRequest.setText(requestRecommendation.getTitle());
        if(!requestRecommendation.getAnswers().isEmpty()) {
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setText(String.valueOf(requestRecommendation.getAnswers().size()));
        } else {
            holder.state.setVisibility(View.INVISIBLE);
        }
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ALGO", "onClick: clicked on: " + position);

                Intent intent = new Intent(mContext, RequestRecommendActivity.class);
                intent.putExtra("requestRecommendation", requestRecommendation);
                intent.putExtra("dateFormat", dateFormat);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestRecommendationsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView requestPhoto;
        TextView titleRequest;
        TextView txtDate;
        TextView state;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            requestPhoto = itemView.findViewById(R.id.imgRequestPhoto);
            titleRequest = itemView.findViewById(R.id.txtRecommendTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            state=itemView.findViewById(R.id.rdbState);
            parentLayout=itemView.findViewById(R.id.parentLayout);
        }
    }
}