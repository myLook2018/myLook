package com.mylook.mylook.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<String>{

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    //private String mAppend; no se usaria. en el tutorial lo pone porque usa universal image loader
    private ArrayList<String> imgUrls;

    public GridImageAdapter(Context context, int layoutResource, ArrayList<String> imgUrls){
        super(context,layoutResource,imgUrls);
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext=context;
        this.layoutResource=layoutResource;
        this.imgUrls=imgUrls;
    }

    private static class ViewHolder{
        SquareImageView image;
        //ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //view holder build pattern (similar to recycler view)

        final ViewHolder holder;
        if (convertView == null){

            convertView = mInflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder();
            //holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.grid_image_progress_bar);
            holder.image = (SquareImageView) convertView.findViewById(R.id.grid_image_view);

            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();
        }

        String imgUrls = getItem(position);

        //aca iria el Glide
        Log.e("CONTEXT2",mContext.toString());
        Log.e("HOLDER", holder.image.toString());
        Glide.with(mContext).load(imgUrls).into(holder.image);

        return convertView;
    }


}
