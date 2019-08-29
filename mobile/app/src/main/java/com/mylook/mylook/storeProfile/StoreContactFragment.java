package com.mylook.mylook.storeProfile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylook.mylook.R;

public class StoreContactFragment extends Fragment {

    private LinearLayout lnlface,lnlInsta,lnlTw;
    private TextView storeLocation, storePhone;
    private TextView txtTwitter, txtInstagram;
    private TextView txtFacebook;


    public StoreContactFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_store, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView){
        Bundle args = getArguments();

        if (args != null) {
            storeLocation = rootView.findViewById(R.id.store_location);
            setStoreLocation(args.getString("location"));

            storePhone = rootView.findViewById(R.id.store_phone);
            setStorePhone(args.getString("phone"));

            lnlface=rootView.findViewById(R.id.lnlFace);
            txtFacebook =rootView.findViewById(R.id.txtFacebook);
            setOnClickFacebook(args.getString("facebook"));

            lnlTw=rootView.findViewById(R.id.lnlTw);
            txtTwitter =rootView.findViewById(R.id.txtTwitter);
            setOnClickTwitter(args.getString("twitter"));

            lnlInsta=rootView.findViewById(R.id.lnlInta);
            txtInstagram =rootView.findViewById(R.id.txtInstagram);
            setOnClickInstagram(args.getString("instagram"));
        }
    }

    private void setStoreLocation(String storeLocation) {
        this.storeLocation.setText(storeLocation);
    }

    private void setStorePhone(String storePhone) {
        this.storePhone.setText(storePhone);
    }

    private void setOnClickFacebook(String txtFacebook) {
        if (!txtFacebook.equals("")) {
            final String finalTxtFacebook = "https://www.facebook.com/"+txtFacebook;
            this.txtFacebook.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                intent.setPackage("com.facebook.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtFacebook)));
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickTwitter(String txtTwitter) {
        if (!txtTwitter.equals("")) {
            final String finalTxtTwitter = "https://twitter.com/"+txtTwitter;
            this.txtTwitter.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtTwitter));
                intent.setPackage("com.twitter.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtTwitter)));
                }

            });
            lnlTw.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickInstagram(String txtInstagram) {
        if (!txtInstagram.equals("")) {
            final String finalTxtInstagram = "https://www.instagram.com/"+txtInstagram;
            this.txtInstagram.setOnClickListener(v -> {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                intent.setPackage("com.instagram.android");
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtInstagram)));
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }
}
