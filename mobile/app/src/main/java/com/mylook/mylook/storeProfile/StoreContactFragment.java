package com.mylook.mylook.storeProfile;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylook.mylook.R;

public class StoreContactFragment extends Fragment {

    private  String storeNameString;
    private  Context context;
    private  FirebaseFirestore dB;
    private LinearLayout lnlface,lnlInsta,lnlTw;
    private TextView storeLocation, storePhone;
    private TextView txtTwitter, txtInstagram;
    private TextView txtFacebook;

    @SuppressLint("ValidFragment")
    public StoreContactFragment(Context context, String storeName) {
        dB = FirebaseFirestore.getInstance();
        storeNameString=storeName;
        this.context=context;

    }

    public StoreContactFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_store, container, false);
        initElements(rootView);
        return rootView;
    }

    public void initElements(View rootView){
        storeLocation = rootView.findViewById(R.id.store_location);
        storePhone = rootView.findViewById(R.id.store_phone);
        txtFacebook =rootView.findViewById(R.id.txtFacebook);
        txtTwitter =rootView.findViewById(R.id.txtTwitter);
        txtInstagram =rootView.findViewById(R.id.txtInstagram);
        lnlface=rootView.findViewById(R.id.lnlFace);
        lnlInsta=rootView.findViewById(R.id.lnlInta);
        lnlTw=rootView.findViewById(R.id.lnlTw);
    }


    public void setStoreLocation(String storeLocation) {
        this.storeLocation.setText(storeLocation);
    }

    public void setStorePhone(String storePhone) {
        this.storePhone.setText(storePhone);
    }

    public void setOnClickFacebook(String txtFacebook) {
        if(txtFacebook!=""){
            if (!txtFacebook.startsWith("http://") && !txtFacebook.startsWith("https://"))
                txtFacebook = "http://" + txtFacebook;
            final String finalTxtFacebook = txtFacebook;
            this.txtFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtFacebook));
                    intent.setPackage("com.facebook.android");
                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtFacebook)));
                    }
                }
            });
            lnlface.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickTwitter( String txtTwitter) {
        if(txtTwitter!=""){
            if (!txtTwitter.startsWith("http://") && !txtTwitter.startsWith("https://"))
                txtTwitter = "http://" + txtTwitter;
            final String finalTxtTwitter = txtTwitter;
            this.txtTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtTwitter));
                    intent.setPackage("com.twitter.android");
                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtTwitter)));
                    }

                }
            });
            lnlTw.setVisibility(View.VISIBLE);
        }
    }

    public void setOnClickInstagram(String txtInstagram) {
        if(txtInstagram!="") {
            if (!txtInstagram.startsWith("http://") && !txtInstagram.startsWith("https://"))
                txtInstagram = "http://" + txtInstagram;
            final String finalTxtInstagram = txtInstagram;
            this.txtInstagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(finalTxtInstagram));
                    intent.setPackage("com.instagram.android");
                    try{
                        startActivity(intent);
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(finalTxtInstagram)));
                    }
                }
            });
            lnlInsta.setVisibility(View.VISIBLE);
        }
    }
}
