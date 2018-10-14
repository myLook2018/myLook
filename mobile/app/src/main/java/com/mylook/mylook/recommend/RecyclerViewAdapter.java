package com.mylook.mylook.recommend;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.RequestRecommendation;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    private List<RequestRecommendation> requestRecommendationsList;

    public RecyclerViewAdapter(Context mContext, List<RequestRecommendation> requestRecommendationsList) {
        this.mContext = mContext;
        this.requestRecommendationsList = requestRecommendationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_recommend, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        final RequestRecommendation requestRecommendation = requestRecommendationsList.get(position);

        Glide.with(mContext).asBitmap().load(requestRecommendation.getRequestPhoto()).into(holder.requestPhoto);
        holder.descriptionRequest.setText(requestRecommendation.getDescription());
        holder.titleRequest.setText(requestRecommendation.getTitle());
        if(requestRecommendation.getState())
            holder.state.setVisibility(View.VISIBLE);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + position);

                Intent intent = new Intent(mContext, RequestRecommendActivity.class);
                intent.putExtra("requestRecommendation", requestRecommendation);
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
        TextView descriptionRequest;
        ImageView state;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            requestPhoto = itemView.findViewById(R.id.imgRequestPhoto);
            titleRequest = itemView.findViewById(R.id.txtRecommendTitle);
            descriptionRequest = itemView.findViewById(R.id.txtRecommendDescpription);
            state=itemView.findViewById(R.id.imgState);
            parentLayout=itemView.findViewById(R.id.parent_layout);
        }
    }
}