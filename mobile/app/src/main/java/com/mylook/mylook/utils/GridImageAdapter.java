package com.mylook.mylook.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.mylook.mylook.R;
import com.mylook.mylook.entities.Article;
import com.mylook.mylook.info.ArticleInfoActivity;

import java.util.ArrayList;

public class GridImageAdapter extends ArrayAdapter<Article>{

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    //private String mAppend; no se usaria. en el tutorial lo pone porque usa universal image loader
    private ArrayList<Article> articles;

    public GridImageAdapter(Context context, int layoutResource, ArrayList<Article> articles){
        super(context,layoutResource,articles);
        mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext=context;
        this.layoutResource=layoutResource;
        this.articles =articles;
    }

    private static class ViewHolder{
        SquareImageView image;
        //ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

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

        String imgUrls = getItem(position).getPicture();

        //aca iria el Glide
        Log.e("CONTEXT2",mContext.toString());
        Log.e("HOLDER", holder.image.toString());
        Glide.with(mContext).load(imgUrls).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ArticleInfoActivity.class);
                intent.putExtra("article", getItem(position));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }


}
